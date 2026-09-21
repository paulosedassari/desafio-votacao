package br.com.paulosedassari.votacao.domain.pauta.model;

import java.time.Instant;

public record Pauta(
        Long id,
        String titulo,
        String descricao,
        Instant criadoEm
) {

    public static Pauta novaPauta(
            String titulo,
            String descricao,
            Instant criadoEm
    ) {
        return new Pauta(null, titulo, descricao, criadoEm);
    }
}
