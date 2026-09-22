package br.com.paulosedassari.votacao.domain.sessao.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class SessaoVotacaoTest {

	@Test
	void deveEstarAbertaAntesDoEncerramento() {
		Instant abertaEm = Instant.parse("2026-09-21T12:00:00Z");
		SessaoVotacao sessao = new SessaoVotacao(
				1L, 10L, abertaEm, Instant.parse("2026-09-21T12:05:00Z"));

		assertThat(sessao.estaAbertaEm(Instant.parse("2026-09-21T12:04:59.999Z"))).isTrue();
	}

	@Test
	void deveEstarEncerradaNoInstanteDeEncerramento() {
		Instant encerraEm = Instant.parse("2026-09-21T12:05:00Z");
		SessaoVotacao sessao = new SessaoVotacao(
				1L, 10L, Instant.parse("2026-09-21T12:00:00Z"), encerraEm);

		assertThat(sessao.estaAbertaEm(encerraEm)).isFalse();
		assertThat(sessao.estaAbertaEm(encerraEm.plusSeconds(1))).isFalse();
	}
}
