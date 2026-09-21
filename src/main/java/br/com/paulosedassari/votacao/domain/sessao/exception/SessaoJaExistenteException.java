package br.com.paulosedassari.votacao.domain.sessao.exception;

public class SessaoJaExistenteException extends RuntimeException {

	public SessaoJaExistenteException(Long pautaId) {
		super("A pauta já possui uma sessão de votação: " + pautaId);
	}
}
