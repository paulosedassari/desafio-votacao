package br.com.paulosedassari.votacao.domain.pauta.exception;

public class PautaNaoEncontradaException extends RuntimeException {

	public PautaNaoEncontradaException(Long pautaId) {
		super("Pauta não encontrada: " + pautaId);
	}
}
