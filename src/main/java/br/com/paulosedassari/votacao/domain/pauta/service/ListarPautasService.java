package br.com.paulosedassari.votacao.domain.pauta.service;

import br.com.paulosedassari.votacao.domain.pauta.port.inbound.ListarPautasUseCase;
import br.com.paulosedassari.votacao.domain.pauta.port.inbound.PautaListada;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;

import java.util.List;

public class ListarPautasService implements ListarPautasUseCase {

    private final PautaPersistencePort pautaPersistencePort;

    public ListarPautasService(PautaPersistencePort pautaPersistencePort) {
        this.pautaPersistencePort = pautaPersistencePort;
    }

    @Override
    public List<PautaListada> executar() {
        return pautaPersistencePort.buscarTodas().stream()
                .map(pauta -> new PautaListada(
                        pauta.id(),
                        pauta.titulo(),
                        pauta.descricao(),
                        pauta.criadoEm()
                ))
                .toList();
    }
}
