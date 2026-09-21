package br.com.paulosedassari.votacao.domain.sessao.exception;

public class SessaoEncerradaException extends RuntimeException {

    public SessaoEncerradaException(Long pautaId) {
        super("A sessão de votação da pauta está encerrada: " + pautaId);
    }
}
