package br.com.paulosedassari.votacao.interfaces.web.pauta.dto;

import br.com.paulosedassari.votacao.domain.pauta.port.inbound.CadastrarPautaCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados da nova pauta")
public record PautaRequest(

        @NotBlank(message = "O título da pauta é obrigatório.")
        @Size(max = 200, message = "O título deve possuir no máximo 200 caracteres.")
        String titulo,

        @NotBlank(message = "A descrição da pauta é obrigatória.")
        @Size(max = 1000, message = "A descrição deve possuir no máximo 1000 caracteres.")
        String descricao
) {

    public CadastrarPautaCommand toCommand() {
        return new CadastrarPautaCommand(titulo, descricao);
    }
}
