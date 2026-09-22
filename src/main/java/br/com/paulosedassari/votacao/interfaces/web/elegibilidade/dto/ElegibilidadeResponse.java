package br.com.paulosedassari.votacao.interfaces.web.elegibilidade.dto;

import br.com.paulosedassari.votacao.domain.elegibilidade.port.inbound.ElegibilidadeConsultada;

public record ElegibilidadeResponse(String status) {

    public static ElegibilidadeResponse toResponse(ElegibilidadeConsultada elegibilidade) {
        return new ElegibilidadeResponse(elegibilidade.status().name());
    }
}
