package br.com.paulosedassari.votacao.domain.pauta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.paulosedassari.votacao.domain.pauta.model.Pauta;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.CadastrarPautaCommand;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.PautaCriada;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;

@ExtendWith(MockitoExtension.class)
class CadastrarPautaServiceTest {

	private static final Instant AGORA = Instant.parse("2026-09-19T13:45:21Z");

	@Mock
	private PautaPersistencePort persistencePort;

	private CadastrarPautaService cadastrarPautaService;

	@BeforeEach
	void configurar() {
		Clock relogio = Clock.fixed(AGORA, ZoneOffset.UTC);
		cadastrarPautaService = new CadastrarPautaService(persistencePort, relogio);
	}

	@Test
	void deveCadastrarPautaComSucesso() {
		when(persistencePort.salvar(any(Pauta.class)))
				.thenAnswer(invocacao -> {
					Pauta pauta = invocacao.getArgument(0);
					return new Pauta(1L, pauta.titulo(), pauta.descricao(), pauta.criadoEm());
				});

		PautaCriada pauta = cadastrarPautaService.executar(new CadastrarPautaCommand(
				"Aprovação do orçamento 2027",
				"Votação referente ao orçamento anual"));

		assertThat(pauta.id()).isEqualTo(1L);
		assertThat(pauta.titulo()).isEqualTo("Aprovação do orçamento 2027");
		assertThat(pauta.descricao()).isEqualTo("Votação referente ao orçamento anual");
		assertThat(pauta.criadoEm()).isEqualTo(AGORA);
	}
}
