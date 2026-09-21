package br.com.paulosedassari.votacao.domain.sessao.model;

import java.time.Instant;

public record SessaoVotacao(
        Long id,
        Long pautaId,
        Instant abertaEm,
        Instant encerraEm
) {

    public static SessaoVotacao nova(Long pautaId, Instant abertaEm, Instant encerraEm) {
        return new SessaoVotacao(null, pautaId, abertaEm, encerraEm);
    }

    public boolean estaAbertaEm(Instant instante) {
        return instante.isBefore(encerraEm);
    }
}
