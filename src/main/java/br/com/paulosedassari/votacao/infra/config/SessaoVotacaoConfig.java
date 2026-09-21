package br.com.paulosedassari.votacao.infra.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import br.com.paulosedassari.votacao.domain.sessao.port.inbound.AbrirSessaoVotacaoUseCase;
import br.com.paulosedassari.votacao.domain.sessao.port.outbound.SessaoVotacaoPersistencePort;
import br.com.paulosedassari.votacao.domain.sessao.service.AbrirSessaoVotacaoService;

@Configuration
public class SessaoVotacaoConfig {

	@Bean
	AbrirSessaoVotacaoUseCase abrirSessaoVotacao(
			PautaPersistencePort pautaPersistencePort,
			SessaoVotacaoPersistencePort sessaoPersistencePort,
			Clock relogio) {
		return new AbrirSessaoVotacaoService(pautaPersistencePort, sessaoPersistencePort, relogio);
	}
}
