package br.com.paulosedassari.votacao.infra.persistence.voto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.paulosedassari.votacao.domain.voto.exception.VotoDuplicadoException;
import br.com.paulosedassari.votacao.domain.voto.model.ValorVoto;
import br.com.paulosedassari.votacao.domain.voto.model.Voto;
import br.com.paulosedassari.votacao.domain.voto.port.outbound.ContagemVotos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class VotoPersistenceAdapterTest {

    private static final Long PAUTA_ID = 42L;
    private static final Instant CRIADO_EM = Instant.parse("2026-09-21T12:00:00Z");

    @Mock
    private SpringDataVotoRepository repository;

    private VotoPersistenceAdapter adapter;

    @BeforeEach
    void configurar() {
        adapter = new VotoPersistenceAdapter(repository);
    }

    @Test
    void deveConsultarExistenciaDoVotoPorPautaEAssociado() {
        when(repository.existsByPautaIdAndAssociadoId(PAUTA_ID, "12345")).thenReturn(true);

        assertThat(adapter.existePorPautaIdEAssociadoId(PAUTA_ID, "12345")).isTrue();
        verify(repository).existsByPautaIdAndAssociadoId(PAUTA_ID, "12345");
    }

    @Test
    void deveSalvarVotoMapeandoDominioEEntidade() {
        var voto = new Voto(null, PAUTA_ID, "12345", ValorVoto.SIM, CRIADO_EM);
        var entidadeSalva = new VotoJpaEntity(9L, PAUTA_ID, "12345", ValorVoto.SIM, CRIADO_EM);
        var entidadeCaptor = ArgumentCaptor.forClass(VotoJpaEntity.class);
        when(repository.saveAndFlush(entidadeCaptor.capture())).thenReturn(entidadeSalva);

        var resultado = adapter.salvar(voto);

        var entidadeRecebida = entidadeCaptor.getValue();
        assertThat(entidadeRecebida.getId()).isNull();
        assertThat(entidadeRecebida.getPautaId()).isEqualTo(PAUTA_ID);
        assertThat(entidadeRecebida.getAssociadoId()).isEqualTo("12345");
        assertThat(entidadeRecebida.getValor()).isEqualTo(ValorVoto.SIM);
        assertThat(entidadeRecebida.getCriadoEm()).isEqualTo(CRIADO_EM);
        assertThat(resultado).isEqualTo(new Voto(9L, PAUTA_ID, "12345", ValorVoto.SIM, CRIADO_EM));
    }

    @Test
    void deveConverterViolacaoDeIntegridadeEmVotoDuplicado() {
        var voto = new Voto(null, PAUTA_ID, "12345", ValorVoto.SIM, CRIADO_EM);
        when(repository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("constraint"));

        assertThatThrownBy(() -> adapter.salvar(voto))
                .isInstanceOf(VotoDuplicadoException.class);
    }

    @Test
    void deveContabilizarVotosAgrupados() {
        var contagemSim = contagem(ValorVoto.SIM, 150);
        var contagemNao = contagem(ValorVoto.NAO, 83);
        when(repository.contabilizarPorPautaId(PAUTA_ID)).thenReturn(List.of(
                contagemSim,
                contagemNao
        ));

        var resultado = adapter.contabilizarPorPautaId(PAUTA_ID);

        assertThat(resultado).isEqualTo(new ContagemVotos(150, 83));
    }

    @Test
    void deveRetornarContagemZeradaQuandoNaoHouverVotos() {
        when(repository.contabilizarPorPautaId(PAUTA_ID)).thenReturn(List.of());

        assertThat(adapter.contabilizarPorPautaId(PAUTA_ID)).isEqualTo(new ContagemVotos(0, 0));
    }

    @Test
    void deveManterZeroParaValorSemVotos() {
        var contagemNao = contagem(ValorVoto.NAO, 12);
        when(repository.contabilizarPorPautaId(PAUTA_ID)).thenReturn(List.of(contagemNao));

        assertThat(adapter.contabilizarPorPautaId(PAUTA_ID)).isEqualTo(new ContagemVotos(0, 12));
    }

    private ContagemVotosProjection contagem(ValorVoto valor, long quantidade) {
        var projection = mock(ContagemVotosProjection.class);
        when(projection.getValor()).thenReturn(valor);
        when(projection.getQuantidade()).thenReturn(quantidade);
        return projection;
    }
}
