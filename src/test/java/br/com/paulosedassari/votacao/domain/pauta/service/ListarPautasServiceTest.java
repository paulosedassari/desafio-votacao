package br.com.paulosedassari.votacao.domain.pauta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.com.paulosedassari.votacao.domain.pauta.model.Pauta;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.PautaListada;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ListarPautasServiceTest {

    @Mock
    private PautaPersistencePort pautaPersistencePort;

    private ListarPautasService service;

    @BeforeEach
    void configurar() {
        service = new ListarPautasService(pautaPersistencePort);
    }

    @Test
    void deveListarTodasAsPautas() {
        var primeira = new Pauta(
                1L,
                "Orçamento 2027",
                "Aprovação do orçamento",
                Instant.parse("2026-09-19T13:45:21Z")
        );
        var segunda = new Pauta(
                2L,
                "Reforma da sede",
                "Aprovação da reforma",
                Instant.parse("2026-09-20T10:00:00Z")
        );
        when(pautaPersistencePort.buscarTodas()).thenReturn(List.of(primeira, segunda));

        var resultado = service.executar();

        assertThat(resultado).containsExactly(
                new PautaListada(1L, primeira.titulo(), primeira.descricao(), primeira.criadoEm()),
                new PautaListada(2L, segunda.titulo(), segunda.descricao(), segunda.criadoEm())
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverPautas() {
        when(pautaPersistencePort.buscarTodas()).thenReturn(List.of());

        assertThat(service.executar()).isEmpty();
    }
}
