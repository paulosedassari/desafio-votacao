package br.com.paulosedassari.votacao.domain.pauta.port.inbound;

public record CadastrarPautaCommand(
        String titulo,
        String descricao
) {
}
