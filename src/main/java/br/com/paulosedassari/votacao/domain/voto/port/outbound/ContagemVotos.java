package br.com.paulosedassari.votacao.domain.voto.port.outbound;

public record ContagemVotos(
        long sim,
        long nao
) {

    public long total() {
        return sim + nao;
    }
}
