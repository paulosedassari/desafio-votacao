package br.com.paulosedassari.votacao.interfaces.web.pauta;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.paulosedassari.votacao.domain.pauta.port.inbound.CadastrarPautaUseCase;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.ListarPautasUseCase;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.PautaCriada;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.PautaListada;

@WebMvcTest(PautaController.class)
class PautaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CadastrarPautaUseCase cadastrarPautaUseCase;

	@MockitoBean
	private ListarPautasUseCase listarPautasUseCase;

	@Test
	void deveListarTodasAsPautas() throws Exception {
		when(listarPautasUseCase.executar()).thenReturn(List.of(
				new PautaListada(
						1L,
						"Orçamento 2027",
						"Aprovação do orçamento",
						Instant.parse("2026-09-19T13:45:21Z")
				),
				new PautaListada(
						2L,
						"Reforma da sede",
						"Aprovação da reforma",
						Instant.parse("2026-09-20T10:00:00Z")
				)
		));

		mockMvc.perform(get("/api/v1/pautas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].titulo").value("Orçamento 2027"))
				.andExpect(jsonPath("$[1].id").value(2))
				.andExpect(jsonPath("$[1].titulo").value("Reforma da sede"));
	}

	@Test
	void deveCriarPauta() throws Exception {
		Instant criadoEm = Instant.parse("2026-09-19T13:45:21Z");
		when(cadastrarPautaUseCase.executar(argThat(comando ->
				comando.titulo().equals("Aprovação do orçamento 2027")
						&& comando.descricao().equals("Votação referente ao orçamento anual"))))
				.thenReturn(new PautaCriada(1L, "Aprovação do orçamento 2027",
						"Votação referente ao orçamento anual", criadoEm));

		mockMvc.perform(post("/api/v1/pautas")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{
							  "titulo": "Aprovação do orçamento 2027",
							  "descricao": "Votação referente ao orçamento anual"
							}
							"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.titulo").value("Aprovação do orçamento 2027"))
				.andExpect(jsonPath("$.descricao").value("Votação referente ao orçamento anual"))
				.andExpect(jsonPath("$.criadoEm").value("2026-09-19T13:45:21Z"));
	}

	@Test
	void deveRejeitarTituloInvalido() throws Exception {
		mockMvc.perform(post("/api/v1/pautas")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{"titulo": "   ", "descricao": "Descrição válida"}
							"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
				.andExpect(jsonPath("$.mensagem").value("Um ou mais campos são inválidos"))
				.andExpect(jsonPath("$.caminho").value("/api/v1/pautas"))
				.andExpect(jsonPath("$.violacoes[0].campo").value("titulo"))
				.andExpect(jsonPath("$.violacoes[0].mensagem").value("O título da pauta é obrigatório."));

		verify(cadastrarPautaUseCase, never()).executar(any());
	}

	@Test
	void deveRejeitarDescricaoInvalida() throws Exception {
		mockMvc.perform(post("/api/v1/pautas")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{"titulo": "Título válido", "descricao": null}
							"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
				.andExpect(jsonPath("$.violacoes[0].campo").value("descricao"));

		verify(cadastrarPautaUseCase, never()).executar(any());
	}

}
