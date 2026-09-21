package br.com.paulosedassari.votacao.domain.sessao.exception;

public class DuracaoSessaoInvalidaException extends RuntimeException {

	public DuracaoSessaoInvalidaException() {
		super("A duração da sessão deve ser positiva");
	}
}
