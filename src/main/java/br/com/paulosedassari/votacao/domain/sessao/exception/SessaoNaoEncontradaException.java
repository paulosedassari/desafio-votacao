package br.com.paulosedassari.votacao.domain.sessao.exception;

public class SessaoNaoEncontradaException extends RuntimeException {

    public SessaoNaoEncontradaException(Long pautaId) {
        super("Sessão de votação não encontrada para a pauta: " + pautaId);
    }
}
