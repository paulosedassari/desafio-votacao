package br.com.paulosedassari.votacao.infra.persistence.sessao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoJaExistenteException;
import br.com.paulosedassari.votacao.domain.sessao.model.SessaoVotacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoPersistenceAdapterTest {

    private static final Long PAUTA_ID = 42L;
    private static final Instant ABERTA_EM = Instant.parse("2026-09-21T12:00:00Z");
    private static final Instant ENCERRA_EM = Instant.parse("2026-09-21T12:05:00Z");

    @Mock
    private SpringDataSessaoVotacaoRepository repository;

    private SessaoVotacaoPersistenceAdapter adapter;

    @BeforeEach
    void configurar() {
        adapter = new SessaoVotacaoPersistenceAdapter(repository);
    }

    @Test
    void deveConsultarExistenciaDaSessaoPorPauta() {
        when(repository.existsByPautaId(PAUTA_ID)).thenReturn(true);

        assertThat(adapter.existePorPautaId(PAUTA_ID)).isTrue();
        verify(repository).existsByPautaId(PAUTA_ID);
    }

    @Test
    void deveBuscarSessaoPorPauta() {
        when(repository.findByPautaId(PAUTA_ID)).thenReturn(Optional.of(entidadeSalva()));

        var resultado = adapter.buscarPorPautaId(PAUTA_ID);

        assertThat(resultado).contains(new SessaoVotacao(7L, PAUTA_ID, ABERTA_EM, ENCERRA_EM));
    }

    @Test
    void deveRetornarVazioQuandoSessaoNaoExistir() {
        when(repository.findByPautaId(PAUTA_ID)).thenReturn(Optional.empty());

        assertThat(adapter.buscarPorPautaId(PAUTA_ID)).isEmpty();
    }

    @Test
    void deveSalvarSessaoMapeandoDominioEEntidade() {
        var sessao = new SessaoVotacao(null, PAUTA_ID, ABERTA_EM, ENCERRA_EM);
        var entidadeCaptor = ArgumentCaptor.forClass(SessaoVotacaoJpaEntity.class);
        when(repository.saveAndFlush(entidadeCaptor.capture())).thenReturn(entidadeSalva());

        var resultado = adapter.salvar(sessao);

        var entidadeRecebida = entidadeCaptor.getValue();
        assertThat(entidadeRecebida.getId()).isNull();
        assertThat(entidadeRecebida.getPautaId()).isEqualTo(PAUTA_ID);
        assertThat(entidadeRecebida.getAbertaEm()).isEqualTo(ABERTA_EM);
        assertThat(entidadeRecebida.getEncerraEm()).isEqualTo(ENCERRA_EM);
        assertThat(resultado).isEqualTo(new SessaoVotacao(7L, PAUTA_ID, ABERTA_EM, ENCERRA_EM));
    }

    @Test
    void deveConverterViolacaoDeIntegridadeEmSessaoJaExistente() {
        var sessao = new SessaoVotacao(null, PAUTA_ID, ABERTA_EM, ENCERRA_EM);
        when(repository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("constraint"));

        assertThatThrownBy(() -> adapter.salvar(sessao))
                .isInstanceOf(SessaoJaExistenteException.class)
                .hasMessageContaining(PAUTA_ID.toString());
    }

    private SessaoVotacaoJpaEntity entidadeSalva() {
        return new SessaoVotacaoJpaEntity(7L, PAUTA_ID, ABERTA_EM, ENCERRA_EM);
    }
}
