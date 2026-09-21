package br.com.paulosedassari.votacao.domain.voto.port.inbound;

public interface ConsultarResultadoVotacaoUseCase {

    ResultadoVotacao executar(Long pautaId);
}
