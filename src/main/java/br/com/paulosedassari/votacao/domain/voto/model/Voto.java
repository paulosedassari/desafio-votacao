package br.com.paulosedassari.votacao.domain.voto.model;

import java.time.Instant;

public record Voto(
        Long id,
        Long pautaId,
        String associadoId,
        ValorVoto valor,
        Instant criadoEm
) {

    public static Voto novo(
            Long pautaId,
            String associadoId,
            ValorVoto valor,
            Instant criadoEm
    ) {
        return new Voto(null, pautaId, associadoId, valor, criadoEm);
    }
}
