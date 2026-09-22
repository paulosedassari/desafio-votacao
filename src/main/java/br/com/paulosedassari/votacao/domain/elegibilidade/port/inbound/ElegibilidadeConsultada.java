package br.com.paulosedassari.votacao.domain.elegibilidade.port.inbound;

import br.com.paulosedassari.votacao.domain.elegibilidade.model.StatusElegibilidade;

public record ElegibilidadeConsultada(StatusElegibilidade status) {
}
