package br.com.paulosedassari.votacao.interfaces.web.pauta.dto;

import br.com.paulosedassari.votacao.domain.pauta.port.inbound.PautaCriada;

import java.time.Instant;

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
}
