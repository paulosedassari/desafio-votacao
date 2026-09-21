package br.com.paulosedassari.votacao.domain.voto.exception;

public class VotoDuplicadoException extends RuntimeException {

    public VotoDuplicadoException() {
        super("O associado já votou nesta pauta");
    }
}
