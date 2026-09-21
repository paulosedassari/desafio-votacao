package br.com.paulosedassari.votacao.domain.pauta.port.inbound;

import java.time.Instant;

public record PautaCriada(
        Long id,
        String titulo,
        String descricao,
        Instant criadoEm
) {
}
