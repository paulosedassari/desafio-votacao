package br.com.paulosedassari.votacao.infra.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.paulosedassari.votacao.domain.pauta.port.inbound.CadastrarPautaUseCase;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.ListarPautasUseCase;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import br.com.paulosedassari.votacao.domain.pauta.service.CadastrarPautaService;
import br.com.paulosedassari.votacao.domain.pauta.service.ListarPautasService;

@Configuration
public class PautaConfig {

	@Bean
	CadastrarPautaUseCase cadastrarPauta(PautaPersistencePort persistencePort, Clock relogio) {
		return new CadastrarPautaService(persistencePort, relogio);
	}

	@Bean
	ListarPautasUseCase listarPautas(PautaPersistencePort persistencePort) {
		return new ListarPautasService(persistencePort);
	}
}
