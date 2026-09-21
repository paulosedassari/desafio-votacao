package br.com.paulosedassari.votacao.interfaces.web.sessao;

import br.com.paulosedassari.votacao.interfaces.web.error.ErroResponse;
import br.com.paulosedassari.votacao.interfaces.web.sessao.dto.SessaoRequest;
import br.com.paulosedassari.votacao.interfaces.web.sessao.dto.SessaoResponse;
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

@Tag(name = "Sessões de votação", description = "Operações relacionadas às sessões de votação")
@RequestMapping("/api/v1/pautas/{pautaId}/sessoes")
public interface SessaoVotacaoOpenApi {

    @Operation(summary = "Abrir sessão", description = "Abre a única sessão de votação de uma pauta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sessão aberta com sucesso"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Duração inválida",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pauta não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "A pauta já possui sessão",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Falha interna",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SessaoResponse> abrirSessao(
            @PathVariable Long pautaId,
            @Valid @RequestBody SessaoRequest request);
}
