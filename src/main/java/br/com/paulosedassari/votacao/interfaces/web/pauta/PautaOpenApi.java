package br.com.paulosedassari.votacao.interfaces.web.pauta;

import br.com.paulosedassari.votacao.interfaces.web.error.ErroResponse;
import br.com.paulosedassari.votacao.interfaces.web.pauta.dto.PautaRequest;
import br.com.paulosedassari.votacao.interfaces.web.pauta.dto.PautaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Pautas", description = "Operações relacionadas às pautas de votação")
@RequestMapping("/api/v1/pautas")
public interface PautaOpenApi {

    @Operation(summary = "Cadastrar pauta", description = "Cadastra uma nova pauta de votação")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pauta cadastrada com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Corpo da requisição inválido",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Falha interna",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PautaResponse> criarPauta(
            @Valid @RequestBody PautaRequest pautaRequest);

    @Operation(summary = "Listar pautas", description = "Retorna todas as pautas cadastradas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pautas consultadas com sucesso"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Falha interna",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<PautaResponse>> listarPautas();
}
