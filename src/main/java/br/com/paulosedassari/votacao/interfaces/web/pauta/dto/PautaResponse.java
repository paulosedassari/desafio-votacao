package br.com.paulosedassari.votacao.interfaces.web.pauta.dto;

import br.com.paulosedassari.votacao.domain.pauta.port.inbound.PautaCriada;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.PautaListada;

import java.time.Instant;
import java.util.List;

public record PautaResponse(
        Long id,
        String titulo,
        String descricao,
        Instant criadoEm
) {

    public static PautaResponse toResponse(PautaCriada pauta) {
        return new PautaResponse(
                pauta.id(),
                pauta.titulo(),
                pauta.descricao(),
                pauta.criadoEm()
        );
    }

    public static PautaResponse toResponse(PautaListada pauta) {
        return new PautaResponse(
                pauta.id(),
                pauta.titulo(),
                pauta.descricao(),
                pauta.criadoEm()
        );
    }

    public static List<PautaResponse> toResponse(List<PautaListada> pautas) {
        return pautas.stream()
                .map(PautaResponse::toResponse)
                .toList();
    }
}
