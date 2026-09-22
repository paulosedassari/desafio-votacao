package br.com.paulosedassari.votacao.domain.sessao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.paulosedassari.votacao.domain.pauta.exception.PautaNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import br.com.paulosedassari.votacao.domain.sessao.exception.DuracaoSessaoInvalidaException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoJaExistenteException;
import br.com.paulosedassari.votacao.domain.sessao.model.SessaoVotacao;
import br.com.paulosedassari.votacao.domain.sessao.port.inbound.AbrirSessaoVotacaoCommand;
import br.com.paulosedassari.votacao.domain.sessao.port.inbound.SessaoVotacaoAberta;
import br.com.paulosedassari.votacao.domain.sessao.port.outbound.SessaoVotacaoPersistencePort;

@ExtendWith(MockitoExtension.class)
class AbrirSessaoVotacaoServiceTest {

	private static final Long PAUTA_ID = 42L;
	private static final Instant AGORA = Instant.parse("2026-09-21T12:00:00Z");

	@Mock
	private PautaPersistencePort pautaPersistencePort;

	@Mock
	private SessaoVotacaoPersistencePort sessaoPersistencePort;

	private AbrirSessaoVotacaoService service;

	@BeforeEach
	void configurar() {
		service = new AbrirSessaoVotacaoService(
				pautaPersistencePort,
				sessaoPersistencePort,
				Clock.fixed(AGORA, ZoneOffset.UTC));
	}

	@Test
	void deveAbrirSessaoComDuracaoInformada() {
		prepararPautaSemSessao();
		when(sessaoPersistencePort.salvar(any())).thenAnswer(invocacao -> {
			SessaoVotacao sessao = invocacao.getArgument(0);
			return new SessaoVotacao(1L, sessao.pautaId(), sessao.abertaEm(), sessao.encerraEm());
		});

		SessaoVotacaoAberta resultado = service.executar(new AbrirSessaoVotacaoCommand(PAUTA_ID, 5L));

		assertThat(resultado.id()).isEqualTo(1L);
		assertThat(resultado.abertaEm()).isEqualTo(AGORA);
		assertThat(resultado.encerraEm()).isEqualTo(AGORA.plusSeconds(300));
	}

	@Test
	void deveUsarUmMinutoQuandoDuracaoNaoForInformada() {
		prepararPautaSemSessao();
		when(sessaoPersistencePort.salvar(any())).thenAnswer(invocacao -> {
			SessaoVotacao sessao = invocacao.getArgument(0);
			return new SessaoVotacao(1L, sessao.pautaId(), sessao.abertaEm(), sessao.encerraEm());
		});

		SessaoVotacaoAberta resultado = service.executar(new AbrirSessaoVotacaoCommand(PAUTA_ID, null));

		assertThat(resultado.encerraEm()).isEqualTo(AGORA.plusSeconds(60));
	}

	@Test
	void naoDeveAbrirSessaoParaPautaInexistente() {
		when(pautaPersistencePort.existePorId(PAUTA_ID)).thenReturn(false);

		assertThatThrownBy(() -> service.executar(new AbrirSessaoVotacaoCommand(PAUTA_ID, 5L)))
				.isInstanceOf(PautaNaoEncontradaException.class)
				.hasMessage("Pauta não encontrada: 42");

		verify(sessaoPersistencePort, never()).salvar(any());
	}

	@Test
	void naoDeveAbrirSessaoComDuracaoInvalida() {
		assertThatThrownBy(() -> service.executar(new AbrirSessaoVotacaoCommand(PAUTA_ID, 0L)))
				.isInstanceOf(DuracaoSessaoInvalidaException.class)
				.hasMessage("A duração da sessão deve ser positiva");

		verify(pautaPersistencePort, never()).existePorId(any());
		verify(sessaoPersistencePort, never()).salvar(any());
	}

	@Test
	void naoDeveAbrirSegundaSessaoParaMesmaPauta() {
		when(pautaPersistencePort.existePorId(PAUTA_ID)).thenReturn(true);
		when(sessaoPersistencePort.existePorPautaId(PAUTA_ID)).thenReturn(true);

		assertThatThrownBy(() -> service.executar(new AbrirSessaoVotacaoCommand(PAUTA_ID, 5L)))
				.isInstanceOf(SessaoJaExistenteException.class)
				.hasMessage("A pauta já possui uma sessão de votação: 42");

		verify(sessaoPersistencePort, never()).salvar(any());
	}

	private void prepararPautaSemSessao() {
		when(pautaPersistencePort.existePorId(PAUTA_ID)).thenReturn(true);
		when(sessaoPersistencePort.existePorPautaId(PAUTA_ID)).thenReturn(false);
	}
}
