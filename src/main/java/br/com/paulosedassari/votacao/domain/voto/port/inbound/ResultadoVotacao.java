package br.com.paulosedassari.votacao.domain.voto.port.inbound;

public record ResultadoVotacao(
        Long pautaId,
        long sim,
        long nao,
        long total
) {
}
