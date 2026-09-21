package br.com.paulosedassari.votacao.interfaces.web.resultado;

import br.com.paulosedassari.votacao.interfaces.web.error.ErroResponse;
import br.com.paulosedassari.votacao.interfaces.web.resultado.dto.ResultadoVotacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Resultados", description = "Operações relacionadas aos resultados das votações")
@RequestMapping("/api/v1/pautas/{pautaId}/resultado")
public interface ResultadoVotacaoOpenApi {

    @Operation(
            summary = "Consultar resultado",
            description = "Contabiliza os votos após o encerramento da sessão de votação"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado contabilizado com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pauta ou sessão não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "A sessão de votação ainda está aberta",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Falha interna",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResultadoVotacaoResponse> consultarResultado(@PathVariable Long pautaId);
}
