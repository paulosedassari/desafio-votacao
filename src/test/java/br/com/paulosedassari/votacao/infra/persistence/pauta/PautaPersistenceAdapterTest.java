package br.com.paulosedassari.votacao.infra.persistence.pauta;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.paulosedassari.votacao.domain.pauta.model.Pauta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
class PautaPersistenceAdapterTest {

    private static final Instant CRIADO_EM = Instant.parse("2026-09-21T12:00:00Z");

    @Mock
    private SpringDataPautaRepository repository;

    private PautaPersistenceAdapter adapter;

    @BeforeEach
    void configurar() {
        adapter = new PautaPersistenceAdapter(repository);
    }

    @Test
    void deveSalvarPautaMapeandoDominioEEntidade() {
        var pauta = new Pauta(null, "Orçamento 2027", "Aprovação do orçamento", CRIADO_EM);
        var entidadeSalva = new PautaJpaEntity(10L, pauta.titulo(), pauta.descricao(), pauta.criadoEm());
        var entidadeCaptor = ArgumentCaptor.forClass(PautaJpaEntity.class);
        when(repository.save(entidadeCaptor.capture())).thenReturn(entidadeSalva);

        var resultado = adapter.salvar(pauta);

        var entidadeRecebida = entidadeCaptor.getValue();
        assertThat(entidadeRecebida.getId()).isNull();
        assertThat(entidadeRecebida.getTitulo()).isEqualTo(pauta.titulo());
        assertThat(entidadeRecebida.getDescricao()).isEqualTo(pauta.descricao());
        assertThat(entidadeRecebida.getCriadoEm()).isEqualTo(CRIADO_EM);
        assertThat(resultado).isEqualTo(new Pauta(10L, pauta.titulo(), pauta.descricao(), CRIADO_EM));
    }

    @Test
    void deveConsultarExistenciaDaPauta() {
        when(repository.existsById(10L)).thenReturn(true);

        assertThat(adapter.existePorId(10L)).isTrue();
        verify(repository).existsById(10L);
    }
}
