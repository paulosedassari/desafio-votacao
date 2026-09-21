package br.com.paulosedassari.votacao.interfaces.web.sessao.dto;

import br.com.paulosedassari.votacao.domain.sessao.port.inbound.AbrirSessaoVotacaoCommand;
import jakarta.validation.constraints.Positive;

public record SessaoRequest(

        @Positive(message = "A duração da sessão deve ser positiva.")
        Long duracaoEmMinutos
) {

    public AbrirSessaoVotacaoCommand toCommand(Long pautaId) {
        return new AbrirSessaoVotacaoCommand(pautaId, duracaoEmMinutos);
    }
}
