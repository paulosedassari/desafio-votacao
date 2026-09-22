package br.com.paulosedassari.votacao.domain.voto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.paulosedassari.votacao.domain.pauta.exception.PautaNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoEmAndamentoException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.sessao.model.SessaoVotacao;
import br.com.paulosedassari.votacao.domain.sessao.port.outbound.SessaoVotacaoPersistencePort;
import br.com.paulosedassari.votacao.domain.voto.port.outbound.ContagemVotos;
import br.com.paulosedassari.votacao.domain.voto.port.outbound.VotoPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ConsultarResultadoVotacaoServiceTest {

    private static final Long PAUTA_ID = 42L;
    private static final Instant AGORA = Instant.parse("2026-09-21T12:00:00Z");

    @Mock
    private PautaPersistencePort pautaPersistencePort;

    @Mock
    private SessaoVotacaoPersistencePort sessaoPersistencePort;

    @Mock
    private VotoPersistencePort votoPersistencePort;

    private ConsultarResultadoVotacaoService service;

    @BeforeEach
    void configurar() {
        service = new ConsultarResultadoVotacaoService(
                pautaPersistencePort,
                sessaoPersistencePort,
                votoPersistencePort,
                Clock.fixed(AGORA, ZoneOffset.UTC)
        );
    }

    @ParameterizedTest
    @MethodSource("contagens")
    void deveContabilizarResultado(long sim, long nao) {
        when(pautaPersistencePort.existePorId(PAUTA_ID)).thenReturn(true);
        when(sessaoPersistencePort.buscarPorPautaId(PAUTA_ID)).thenReturn(Optional.of(sessaoEncerrada()));
        when(votoPersistencePort.contabilizarPorPautaId(PAUTA_ID)).thenReturn(new ContagemVotos(sim, nao));

        var resultado = service.executar(PAUTA_ID);

        assertThat(resultado.pautaId()).isEqualTo(PAUTA_ID);
        assertThat(resultado.sim()).isEqualTo(sim);
        assertThat(resultado.nao()).isEqualTo(nao);
        assertThat(resultado.total()).isEqualTo(sim + nao);
    }

    @Test
    void deveRejeitarPautaInexistente() {
        when(pautaPersistencePort.existePorId(PAUTA_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.executar(PAUTA_ID))
                .isInstanceOf(PautaNaoEncontradaException.class);

        verify(sessaoPersistencePort, never()).buscarPorPautaId(PAUTA_ID);
        verify(votoPersistencePort, never()).contabilizarPorPautaId(PAUTA_ID);
    }

    @Test
    void deveRejeitarSessaoInexistente() {
        when(pautaPersistencePort.existePorId(PAUTA_ID)).thenReturn(true);
        when(sessaoPersistencePort.buscarPorPautaId(PAUTA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.executar(PAUTA_ID))
                .isInstanceOf(SessaoNaoEncontradaException.class);

        verify(votoPersistencePort, never()).contabilizarPorPautaId(PAUTA_ID);
    }

    @Test
    void deveRejeitarConsultaEnquantoSessaoEstiverAberta() {
        when(pautaPersistencePort.existePorId(PAUTA_ID)).thenReturn(true);
        when(sessaoPersistencePort.buscarPorPautaId(PAUTA_ID)).thenReturn(Optional.of(
                new SessaoVotacao(1L, PAUTA_ID, AGORA.minusSeconds(30), AGORA.plusSeconds(30))
        ));

        assertThatThrownBy(() -> service.executar(PAUTA_ID))
                .isInstanceOf(SessaoEmAndamentoException.class);

        verify(votoPersistencePort, never()).contabilizarPorPautaId(PAUTA_ID);
    }

    private SessaoVotacao sessaoEncerrada() {
        return new SessaoVotacao(1L, PAUTA_ID, AGORA.minusSeconds(120), AGORA);
    }

    private static Stream<Arguments> contagens() {
        return Stream.of(
                Arguments.of(0L, 0L),
                Arguments.of(10L, 0L),
                Arguments.of(0L, 8L),
                Arguments.of(5L, 5L),
                Arguments.of(150L, 83L)
        );
    }
}
