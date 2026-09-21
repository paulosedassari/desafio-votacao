package br.com.paulosedassari.votacao.interfaces.web.resultado.dto;

import br.com.paulosedassari.votacao.domain.voto.port.inbound.ResultadoVotacao;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contabilização final dos votos de uma pauta")
public record ResultadoVotacaoResponse(
        Long pautaId,
        long sim,
        long nao,
        long total
) {

    public static ResultadoVotacaoResponse toResponse(ResultadoVotacao resultado) {
        return new ResultadoVotacaoResponse(
                resultado.pautaId(),
                resultado.sim(),
                resultado.nao(),
                resultado.total()
        );
    }
}
