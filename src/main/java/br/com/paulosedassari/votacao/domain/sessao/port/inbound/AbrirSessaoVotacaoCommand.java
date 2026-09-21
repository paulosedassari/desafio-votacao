package br.com.paulosedassari.votacao.domain.sessao.port.inbound;

public record AbrirSessaoVotacaoCommand(Long pautaId, Long duracaoEmMinutos) {
}
