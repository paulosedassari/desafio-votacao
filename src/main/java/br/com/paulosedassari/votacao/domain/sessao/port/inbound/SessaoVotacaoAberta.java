package br.com.paulosedassari.votacao.domain.sessao.port.inbound;

import java.time.Instant;

public record SessaoVotacaoAberta(Long id, Long pautaId, Instant abertaEm, Instant encerraEm) {
}
