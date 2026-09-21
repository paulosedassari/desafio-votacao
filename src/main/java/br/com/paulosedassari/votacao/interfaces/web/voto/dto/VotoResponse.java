package br.com.paulosedassari.votacao.interfaces.web.voto.dto;

import br.com.paulosedassari.votacao.domain.voto.model.ValorVoto;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.VotoRegistrado;

import java.time.Instant;

public record VotoResponse(
        Long id,
        Long pautaId,
        String associadoId,
        ValorVoto voto,
        Instant criadoEm
) {

    public static VotoResponse toResponse(VotoRegistrado voto) {
        return new VotoResponse(
                voto.id(),
                voto.pautaId(),
                voto.associadoId(),
                voto.valor(),
                voto.criadoEm()
        );
    }
}
