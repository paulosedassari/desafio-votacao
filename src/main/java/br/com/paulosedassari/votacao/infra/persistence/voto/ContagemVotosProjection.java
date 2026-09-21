package br.com.paulosedassari.votacao.infra.persistence.voto;

import br.com.paulosedassari.votacao.domain.voto.model.ValorVoto;

interface ContagemVotosProjection {

    ValorVoto getValor();

    long getQuantidade();
}
