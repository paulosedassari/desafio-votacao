package br.com.paulosedassari.votacao.domain.elegibilidade.exception;

public class CpfInvalidoException extends RuntimeException {

    public CpfInvalidoException() {
        super("CPF inválido");
    }
}
