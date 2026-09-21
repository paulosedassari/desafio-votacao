package br.com.paulosedassari.votacao.domain.voto.port.inbound;

import br.com.paulosedassari.votacao.domain.voto.model.ValorVoto;

public record RegistrarVotoCommand(
        Long pautaId,
        String associadoId,
        ValorVoto valor
) {
}
