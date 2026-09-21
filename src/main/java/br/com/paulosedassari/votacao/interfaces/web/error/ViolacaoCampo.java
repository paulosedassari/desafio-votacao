package br.com.paulosedassari.votacao.interfaces.web.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Erro de validação associado a um campo")
public record ViolacaoCampo(
        String campo,
        String mensagem
) {
}
