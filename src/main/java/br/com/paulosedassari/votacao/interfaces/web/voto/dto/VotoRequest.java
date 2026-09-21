package br.com.paulosedassari.votacao.interfaces.web.voto.dto;

import br.com.paulosedassari.votacao.domain.voto.model.ValorVoto;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.RegistrarVotoCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VotoRequest(

        @NotBlank(message = "O identificador do associado é obrigatório.")
        @Size(max = 100, message = "O identificador do associado deve possuir no máximo 100 caracteres.")
        String associadoId,

        @NotNull(message = "O voto é obrigatório.")
        ValorVoto voto
) {

    public RegistrarVotoCommand toCommand(Long pautaId) {
        return new RegistrarVotoCommand(pautaId, associadoId, voto);
    }
}
