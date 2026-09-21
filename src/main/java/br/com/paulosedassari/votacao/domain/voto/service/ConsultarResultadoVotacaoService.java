package br.com.paulosedassari.votacao.domain.voto.service;

import br.com.paulosedassari.votacao.domain.pauta.exception.PautaNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoEmAndamentoException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.sessao.port.outbound.SessaoVotacaoPersistencePort;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.ConsultarResultadoVotacaoUseCase;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.ResultadoVotacao;
import br.com.paulosedassari.votacao.domain.voto.port.outbound.VotoPersistencePort;

import java.time.Clock;
import java.time.Instant;

public class ConsultarResultadoVotacaoService implements ConsultarResultadoVotacaoUseCase {

    private final PautaPersistencePort pautaPersistencePort;
    private final SessaoVotacaoPersistencePort sessaoPersistencePort;
    private final VotoPersistencePort votoPersistencePort;
    private final Clock relogio;

    public ConsultarResultadoVotacaoService(
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
    public ResultadoVotacao executar(Long pautaId) {
        if (!pautaPersistencePort.existePorId(pautaId)) {
            throw new PautaNaoEncontradaException(pautaId);
        }

        var sessao = sessaoPersistencePort.buscarPorPautaId(pautaId)
                .orElseThrow(() -> new SessaoNaoEncontradaException(pautaId));

        if (sessao.estaAbertaEm(Instant.now(relogio))) {
            throw new SessaoEmAndamentoException(pautaId);
        }

        var contagem = votoPersistencePort.contabilizarPorPautaId(pautaId);

        return new ResultadoVotacao(
                pautaId,
                contagem.sim(),
                contagem.nao(),
                contagem.total()
        );
    }
}
