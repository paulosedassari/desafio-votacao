package br.com.paulosedassari.votacao.domain.sessao.port.outbound;

import br.com.paulosedassari.votacao.domain.sessao.model.SessaoVotacao;

public interface SessaoVotacaoPersistencePort {

	boolean existePorPautaId(Long pautaId);

	SessaoVotacao salvar(SessaoVotacao sessao);
}
