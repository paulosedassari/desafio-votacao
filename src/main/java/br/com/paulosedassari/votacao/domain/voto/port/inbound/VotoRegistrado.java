package br.com.paulosedassari.votacao.domain.voto.port.inbound;

import br.com.paulosedassari.votacao.domain.voto.model.ValorVoto;

import java.time.Instant;

public record VotoRegistrado(
        Long id,
        Long pautaId,
        String associadoId,
        ValorVoto valor,
        Instant criadoEm
) {
}
