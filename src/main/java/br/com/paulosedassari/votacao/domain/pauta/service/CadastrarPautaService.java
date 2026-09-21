package br.com.paulosedassari.votacao.domain.pauta.service;

import br.com.paulosedassari.votacao.domain.pauta.model.Pauta;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.CadastrarPautaCommand;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.CadastrarPautaUseCase;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.PautaCriada;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;

import java.time.Clock;
import java.time.Instant;

public class CadastrarPautaService implements CadastrarPautaUseCase {

    private final PautaPersistencePort persistencePort;
    private final Clock relogio;

    public CadastrarPautaService(PautaPersistencePort persistencePort, Clock relogio) {
        this.persistencePort = persistencePort;
        this.relogio = relogio;
    }

    @Override
    public PautaCriada executar(CadastrarPautaCommand command) {
        var pauta = Pauta.novaPauta(command.titulo(), command.descricao(), Instant.now(relogio));
        var pautaSalva = persistencePort.salvar(pauta);

        return new PautaCriada(
                pautaSalva.id(),
                pautaSalva.titulo(),
                pautaSalva.descricao(),
                pautaSalva.criadoEm()
        );
    }
}
