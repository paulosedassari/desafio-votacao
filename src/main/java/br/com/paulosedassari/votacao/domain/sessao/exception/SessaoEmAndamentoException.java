package br.com.paulosedassari.votacao.domain.sessao.exception;

public class SessaoEmAndamentoException extends RuntimeException {

    public SessaoEmAndamentoException(Long pautaId) {
        super("A sessão de votação da pauta %d ainda está aberta".formatted(pautaId));
    }
}
