package br.com.paulosedassari.votacao.interfaces.web.voto;

import br.com.paulosedassari.votacao.interfaces.web.error.ErroResponse;
import br.com.paulosedassari.votacao.interfaces.web.voto.dto.VotoRequest;
import br.com.paulosedassari.votacao.interfaces.web.voto.dto.VotoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Votos", description = "Operações relacionadas aos votos")
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
public interface VotoOpenApi {

    @Operation(summary = "Registrar voto", description = "Registra o voto de um associado em uma pauta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados do voto inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pauta ou sessão não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Sessão encerrada ou voto já registrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Falha interna",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<VotoResponse> registrarVoto(
            @PathVariable Long pautaId,
            @Valid @RequestBody VotoRequest request);
}
