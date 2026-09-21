package br.com.paulosedassari.votacao.interfaces.web.sessao.dto;

import java.time.Instant;

import br.com.paulosedassari.votacao.domain.sessao.port.inbound.SessaoVotacaoAberta;

public record SessaoResponse(Long id, Long pautaId, Instant abertaEm, Instant encerraEm) {

	public static SessaoResponse toResponse(SessaoVotacaoAberta sessao) {
		return new SessaoResponse(
				sessao.id(), sessao.pautaId(), sessao.abertaEm(), sessao.encerraEm());
	}
}
