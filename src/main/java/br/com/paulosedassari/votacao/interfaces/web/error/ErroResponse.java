package br.com.paulosedassari.votacao.interfaces.web.error;

import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta padronizada de erro")
public record ErroResponse(
		Instant dataHora,
		int status,
		String codigo,
		String mensagem,
		String caminho,
		List<ViolacaoCampo> violacoes
) {
}
