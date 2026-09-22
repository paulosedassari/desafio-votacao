package br.com.paulosedassari.votacao.interfaces.web.voto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoEncerradaException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.voto.exception.VotoDuplicadoException;
import br.com.paulosedassari.votacao.domain.voto.model.ValorVoto;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.RegistrarVotoUseCase;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.VotoRegistrado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

@WebMvcTest(VotoController.class)
class VotoControllerTest {

    @MockitoBean
    private RegistrarVotoUseCase useCase;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRegistrarVoto() throws Exception {
        when(useCase.executar(argThat(command ->
                command.pautaId().equals(42L)
                        && command.associadoId().equals("12345")
                        && command.valor() == ValorVoto.SIM)))
                .thenReturn(new VotoRegistrado(
                        1L,
                        42L,
                        "12345",
                        ValorVoto.SIM,
                        Instant.parse("2026-09-21T12:00:00Z")
                ));

        mockMvc.perform(post("/api/v1/pautas/42/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"12345\", \"voto\": \"SIM\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.pautaId").value(42))
                .andExpect(jsonPath("$.associadoId").value("12345"))
                .andExpect(jsonPath("$.voto").value("SIM"))
                .andExpect(jsonPath("$.criadoEm").value("2026-09-21T12:00:00Z"));
    }

    @Test
    void deveRejeitarAssociadoInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/pautas/42/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"   \", \"voto\": \"SIM\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.violacoes[0].campo").value("associadoId"));

        verify(useCase, never()).executar(any());
    }

    @Test
    void deveRejeitarValorDeVotoInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/pautas/42/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"12345\", \"voto\": \"TALVEZ\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.violacoes[0].campo").value("voto"))
                .andExpect(jsonPath("$.violacoes[0].mensagem").value("O voto deve ser SIM ou NAO."));

        verify(useCase, never()).executar(any());
    }

    @Test
    void deveRetornarNaoEncontradoQuandoNaoHouverSessao() throws Exception {
        when(useCase.executar(any())).thenThrow(new SessaoNaoEncontradaException(42L));

        mockMvc.perform(post("/api/v1/pautas/42/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"12345\", \"voto\": \"SIM\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("SESSAO_NAO_ENCONTRADA"));
    }

    @Test
    void deveRetornarConflitoQuandoSessaoEstiverEncerrada() throws Exception {
        when(useCase.executar(any())).thenThrow(new SessaoEncerradaException(42L));

        mockMvc.perform(post("/api/v1/pautas/42/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"12345\", \"voto\": \"SIM\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("SESSAO_ENCERRADA"));
    }

    @Test
    void deveRetornarConflitoParaVotoDuplicado() throws Exception {
        when(useCase.executar(any())).thenThrow(new VotoDuplicadoException());

        mockMvc.perform(post("/api/v1/pautas/42/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"12345\", \"voto\": \"NAO\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("VOTO_JA_REGISTRADO"));
    }
}
