package br.com.paulosedassari.votacao.interfaces.web.elegibilidade;

import br.com.paulosedassari.votacao.interfaces.web.elegibilidade.dto.ElegibilidadeResponse;
import br.com.paulosedassari.votacao.interfaces.web.error.ErroResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Elegibilidade", description = "Simula a consulta de elegibilidade para votação")
@RequestMapping("/api/v1/elegibilidade")
public interface ElegibilidadeOpenApi {

    @Operation(
            summary = "Consultar elegibilidade",
            description = "Valida o CPF e retorna aleatoriamente se ele está apto a votar"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Elegibilidade consultada com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parâmetro CPF não informado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "CPF inválido",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Falha interna",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ElegibilidadeResponse> consultar(@RequestParam String cpf);
}
