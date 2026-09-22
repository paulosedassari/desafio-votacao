package br.com.paulosedassari.votacao.infra.config;

import br.com.paulosedassari.votacao.domain.elegibilidade.port.inbound.ConsultarElegibilidadeUseCase;
import br.com.paulosedassari.votacao.domain.elegibilidade.port.outbound.ValidadorElegibilidade;
import br.com.paulosedassari.votacao.domain.elegibilidade.service.ConsultarElegibilidadeService;
import br.com.paulosedassari.votacao.infra.external.elegibilidade.FakeValidadorElegibilidadeAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.random.RandomGenerator;

@Configuration
public class ElegibilidadeConfig {

    @Bean
    ValidadorElegibilidade validadorElegibilidade() {
        return new FakeValidadorElegibilidadeAdapter(RandomGenerator.getDefault());
    }

    @Bean
    ConsultarElegibilidadeUseCase consultarElegibilidade(ValidadorElegibilidade validadorElegibilidade) {
        return new ConsultarElegibilidadeService(validadorElegibilidade);
    }
}
