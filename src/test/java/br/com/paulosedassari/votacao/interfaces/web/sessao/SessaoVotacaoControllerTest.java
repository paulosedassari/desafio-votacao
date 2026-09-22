package br.com.paulosedassari.votacao.interfaces.web.sessao;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.paulosedassari.votacao.domain.pauta.exception.PautaNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoJaExistenteException;
import br.com.paulosedassari.votacao.domain.sessao.port.inbound.AbrirSessaoVotacaoUseCase;
import br.com.paulosedassari.votacao.domain.sessao.port.inbound.SessaoVotacaoAberta;

@WebMvcTest(SessaoVotacaoController.class)
class SessaoVotacaoControllerTest {

	@MockitoBean
	private AbrirSessaoVotacaoUseCase useCase;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void deveAbrirSessao() throws Exception {
		when(useCase.executar(argThat(command ->
				command.pautaId().equals(42L) && command.duracaoEmMinutos().equals(5L))))
				.thenReturn(new SessaoVotacaoAberta(
						1L,
						42L,
						Instant.parse("2026-09-21T12:00:00Z"),
						Instant.parse("2026-09-21T12:05:00Z")));

		mockMvc.perform(post("/api/v1/pautas/42/sessoes")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"duracaoEmMinutos\": 5}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.pautaId").value(42))
				.andExpect(jsonPath("$.abertaEm").value("2026-09-21T12:00:00Z"))
				.andExpect(jsonPath("$.encerraEm").value("2026-09-21T12:05:00Z"));
	}

	@Test
	void deveAceitarDuracaoOmitida() throws Exception {
		when(useCase.executar(argThat(command ->
				command.pautaId().equals(42L) && command.duracaoEmMinutos() == null)))
				.thenReturn(new SessaoVotacaoAberta(
						1L,
						42L,
						Instant.parse("2026-09-21T12:00:00Z"),
						Instant.parse("2026-09-21T12:01:00Z")));

		mockMvc.perform(post("/api/v1/pautas/42/sessoes")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.encerraEm").value("2026-09-21T12:01:00Z"));
	}

	@Test
	void deveRejeitarDuracaoInvalida() throws Exception {
		mockMvc.perform(post("/api/v1/pautas/42/sessoes")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"duracaoEmMinutos\": 0}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
				.andExpect(jsonPath("$.violacoes[0].campo").value("duracaoEmMinutos"));
	}

	@Test
	void deveRetornarNaoEncontradoParaPautaInexistente() throws Exception {
		when(useCase.executar(argThat(command -> command.pautaId().equals(99L))))
				.thenThrow(new PautaNaoEncontradaException(99L));

		mockMvc.perform(post("/api/v1/pautas/99/sessoes")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{}"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.codigo").value("PAUTA_NAO_ENCONTRADA"));
	}

	@Test
	void deveRetornarConflitoParaSegundaSessao() throws Exception {
		when(useCase.executar(argThat(command -> command.pautaId().equals(42L))))
				.thenThrow(new SessaoJaExistenteException(42L));

		mockMvc.perform(post("/api/v1/pautas/42/sessoes")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.codigo").value("SESSAO_JA_EXISTENTE"));
	}
}
