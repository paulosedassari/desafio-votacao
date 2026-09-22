package br.com.paulosedassari.votacao.domain.voto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.paulosedassari.votacao.domain.pauta.exception.PautaNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoEncerradaException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.sessao.model.SessaoVotacao;
import br.com.paulosedassari.votacao.domain.sessao.port.outbound.SessaoVotacaoPersistencePort;
import br.com.paulosedassari.votacao.domain.voto.exception.VotoDuplicadoException;
import br.com.paulosedassari.votacao.domain.voto.model.ValorVoto;
import br.com.paulosedassari.votacao.domain.voto.model.Voto;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.RegistrarVotoCommand;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.VotoRegistrado;
import br.com.paulosedassari.votacao.domain.voto.port.outbound.VotoPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RegistrarVotoServiceTest {

    private static final Long PAUTA_ID = 42L;
    private static final String ASSOCIADO_ID = "12345";
    private static final Instant AGORA = Instant.parse("2026-09-21T12:00:00Z");

    @Mock
    private PautaPersistencePort pautaPersistencePort;

    @Mock
    private SessaoVotacaoPersistencePort sessaoPersistencePort;

    @Mock
    private VotoPersistencePort votoPersistencePort;

    private RegistrarVotoService service;

    @BeforeEach
    void configurar() {
        service = new RegistrarVotoService(
                pautaPersistencePort,
                sessaoPersistencePort,
                votoPersistencePort,
                Clock.fixed(AGORA, ZoneOffset.UTC)
        );
    }

    @ParameterizedTest
    @EnumSource(ValorVoto.class)
    void deveRegistrarVoto(ValorVoto valor) {
        prepararSessaoAberta();
        when(votoPersistencePort.existePorPautaIdEAssociadoId(PAUTA_ID, ASSOCIADO_ID)).thenReturn(false);
        when(votoPersistencePort.salvar(any())).thenAnswer(invocacao -> {
            Voto voto = invocacao.getArgument(0);
            return new Voto(1L, voto.pautaId(), voto.associadoId(), voto.valor(), voto.criadoEm());
        });

        VotoRegistrado resultado = service.executar(
                new RegistrarVotoCommand(PAUTA_ID, ASSOCIADO_ID, valor)
        );

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.valor()).isEqualTo(valor);
        assertThat(resultado.criadoEm()).isEqualTo(AGORA);
    }

    @Test
    void naoDeveRegistrarVotoParaPautaInexistente() {
        when(pautaPersistencePort.existePorId(PAUTA_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.executar(commandSim()))
                .isInstanceOf(PautaNaoEncontradaException.class);

        verify(sessaoPersistencePort, never()).buscarPorPautaId(any());
        verify(votoPersistencePort, never()).salvar(any());
    }

    @Test
    void naoDeveRegistrarVotoSemSessao() {
        when(pautaPersistencePort.existePorId(PAUTA_ID)).thenReturn(true);
        when(sessaoPersistencePort.buscarPorPautaId(PAUTA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.executar(commandSim()))
                .isInstanceOf(SessaoNaoEncontradaException.class);

        verify(votoPersistencePort, never()).salvar(any());
    }

    @Test
    void naoDeveRegistrarVotoNoInstanteDeEncerramento() {
        when(pautaPersistencePort.existePorId(PAUTA_ID)).thenReturn(true);
        when(sessaoPersistencePort.buscarPorPautaId(PAUTA_ID)).thenReturn(Optional.of(
                new SessaoVotacao(1L, PAUTA_ID, AGORA.minusSeconds(60), AGORA)
        ));

        assertThatThrownBy(() -> service.executar(commandSim()))
                .isInstanceOf(SessaoEncerradaException.class);

        verify(votoPersistencePort, never()).salvar(any());
    }

    @Test
    void naoDeveRegistrarVotoDuplicado() {
        prepararSessaoAberta();
        when(votoPersistencePort.existePorPautaIdEAssociadoId(PAUTA_ID, ASSOCIADO_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.executar(commandSim()))
                .isInstanceOf(VotoDuplicadoException.class)
                .hasMessage("O associado já votou nesta pauta");

        verify(votoPersistencePort, never()).salvar(any());
    }

    private void prepararSessaoAberta() {
        when(pautaPersistencePort.existePorId(PAUTA_ID)).thenReturn(true);
        when(sessaoPersistencePort.buscarPorPautaId(PAUTA_ID)).thenReturn(Optional.of(
                new SessaoVotacao(1L, PAUTA_ID, AGORA.minusSeconds(60), AGORA.plusSeconds(60))
        ));
    }

    private RegistrarVotoCommand commandSim() {
        return new RegistrarVotoCommand(PAUTA_ID, ASSOCIADO_ID, ValorVoto.SIM);
    }
}
