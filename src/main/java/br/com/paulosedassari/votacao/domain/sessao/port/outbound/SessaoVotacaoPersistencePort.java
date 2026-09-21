package br.com.paulosedassari.votacao.domain.sessao.port.outbound;

import br.com.paulosedassari.votacao.domain.sessao.model.SessaoVotacao;

import java.util.Optional;

public interface SessaoVotacaoPersistencePort {

	boolean existePorPautaId(Long pautaId);

	Optional<SessaoVotacao> buscarPorPautaId(Long pautaId);

	SessaoVotacao salvar(SessaoVotacao sessao);
}
