package br.com.paulosedassari.votacao.domain.sessao.port.inbound;

public interface AbrirSessaoVotacaoUseCase {

	SessaoVotacaoAberta executar(AbrirSessaoVotacaoCommand command);
}
