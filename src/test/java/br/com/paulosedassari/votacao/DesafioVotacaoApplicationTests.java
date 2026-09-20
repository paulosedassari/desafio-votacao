package br.com.paulosedassari.votacao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.assertj.core.api.Assertions.assertThat;

class DesafioVotacaoApplicationTests {

	@Test
	void devePossuirConfiguracaoDeAplicacaoSpringBoot() {
		assertThat(DesafioVotacaoApplication.class)
				.hasAnnotation(SpringBootApplication.class);
	}

}
