package br.com.paulosedassari.votacao.domain.sessao.service;

import br.com.paulosedassari.votacao.domain.pauta.exception.PautaNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import br.com.paulosedassari.votacao.domain.sessao.exception.DuracaoSessaoInvalidaException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoJaExistenteException;
import br.com.paulosedassari.votacao.domain.sessao.model.SessaoVotacao;
import br.com.paulosedassari.votacao.domain.sessao.port.inbound.AbrirSessaoVotacaoCommand;
import br.com.paulosedassari.votacao.domain.sessao.port.inbound.AbrirSessaoVotacaoUseCase;
import br.com.paulosedassari.votacao.domain.sessao.port.inbound.SessaoVotacaoAberta;
import br.com.paulosedassari.votacao.domain.sessao.port.outbound.SessaoVotacaoPersistencePort;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AbrirSessaoVotacaoService implements AbrirSessaoVotacaoUseCase {

    private static final long DURACAO_PADRAO_EM_MINUTOS = 1L;

    private final Clock relogio;
    private final PautaPersistencePort pautaPersistencePort;
    private final SessaoVotacaoPersistencePort sessaoPersistencePort;

    public AbrirSessaoVotacaoService(
            PautaPersistencePort pautaPersistencePort,
            SessaoVotacaoPersistencePort sessaoPersistencePort,
            Clock relogio
    ) {
        this.pautaPersistencePort = pautaPersistencePort;
        this.sessaoPersistencePort = sessaoPersistencePort;
        this.relogio = relogio;
    }

    @Override
    public SessaoVotacaoAberta executar(AbrirSessaoVotacaoCommand command) {
        Long pautaId = command.pautaId();
        long duracao = command.duracaoEmMinutos() == null
                ? DURACAO_PADRAO_EM_MINUTOS
                : command.duracaoEmMinutos();

        if (duracao <= 0) {
            throw new DuracaoSessaoInvalidaException();
        }

        if (!pautaPersistencePort.existePorId(pautaId)) {
            throw new PautaNaoEncontradaException(pautaId);
        }
        if (sessaoPersistencePort.existePorPautaId(pautaId)) {
            throw new SessaoJaExistenteException(pautaId);
        }

        Instant abertaEm = Instant.now(relogio);
        Instant encerraEm = abertaEm.plus(duracao, ChronoUnit.MINUTES);

        var novaSessao = SessaoVotacao.nova(pautaId, abertaEm, encerraEm);
        var sessaoSalva = sessaoPersistencePort.salvar(novaSessao);

        return new SessaoVotacaoAberta(
                sessaoSalva.id(),
                sessaoSalva.pautaId(),
                sessaoSalva.abertaEm(),
                sessaoSalva.encerraEm()
        );
    }
}
