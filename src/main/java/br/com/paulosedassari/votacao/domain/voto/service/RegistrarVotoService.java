package br.com.paulosedassari.votacao.domain.voto.service;

import br.com.paulosedassari.votacao.domain.pauta.exception.PautaNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoEncerradaException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.sessao.port.outbound.SessaoVotacaoPersistencePort;
import br.com.paulosedassari.votacao.domain.voto.exception.VotoDuplicadoException;
import br.com.paulosedassari.votacao.domain.voto.model.Voto;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.RegistrarVotoCommand;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.RegistrarVotoUseCase;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.VotoRegistrado;
import br.com.paulosedassari.votacao.domain.voto.port.outbound.VotoPersistencePort;

import java.time.Clock;
import java.time.Instant;

public class RegistrarVotoService implements RegistrarVotoUseCase {

    private final Clock relogio;
    private final PautaPersistencePort pautaPersistencePort;
    private final SessaoVotacaoPersistencePort sessaoPersistencePort;
    private final VotoPersistencePort votoPersistencePort;

    public RegistrarVotoService(
            PautaPersistencePort pautaPersistencePort,
            SessaoVotacaoPersistencePort sessaoPersistencePort,
            VotoPersistencePort votoPersistencePort,
            Clock relogio
    ) {
        this.pautaPersistencePort = pautaPersistencePort;
        this.sessaoPersistencePort = sessaoPersistencePort;
        this.votoPersistencePort = votoPersistencePort;
        this.relogio = relogio;
    }

    @Override
    public VotoRegistrado executar(RegistrarVotoCommand command) {
        Long pautaId = command.pautaId();

        if (!pautaPersistencePort.existePorId(pautaId)) {
            throw new PautaNaoEncontradaException(pautaId);
        }

        var sessao = sessaoPersistencePort.buscarPorPautaId(pautaId)
                .orElseThrow(() -> new SessaoNaoEncontradaException(pautaId));

        Instant agora = Instant.now(relogio);

        if (!sessao.estaAbertaEm(agora)) {
            throw new SessaoEncerradaException(pautaId);
        }

        if (votoPersistencePort.existePorPautaIdEAssociadoId(pautaId, command.associadoId())) {
            throw new VotoDuplicadoException();
        }

        var novoVoto = Voto.novo(pautaId, command.associadoId(), command.valor(), agora);
        var votoSalvo = votoPersistencePort.salvar(novoVoto);

        return new VotoRegistrado(
                votoSalvo.id(),
                votoSalvo.pautaId(),
                votoSalvo.associadoId(),
                votoSalvo.valor(),
                votoSalvo.criadoEm()
        );
    }
}
