package br.com.paulosedassari.votacao.infra.config;

import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import br.com.paulosedassari.votacao.domain.sessao.port.outbound.SessaoVotacaoPersistencePort;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.RegistrarVotoUseCase;
import br.com.paulosedassari.votacao.domain.voto.port.outbound.VotoPersistencePort;
import br.com.paulosedassari.votacao.domain.voto.service.RegistrarVotoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class VotoConfig {

    @Bean
    RegistrarVotoUseCase registrarVoto(
            PautaPersistencePort pautaPersistencePort,
            SessaoVotacaoPersistencePort sessaoPersistencePort,
            VotoPersistencePort votoPersistencePort,
            Clock relogio
    ) {
        return new RegistrarVotoService(
                pautaPersistencePort,
                sessaoPersistencePort,
                votoPersistencePort,
                relogio
        );
    }
}
