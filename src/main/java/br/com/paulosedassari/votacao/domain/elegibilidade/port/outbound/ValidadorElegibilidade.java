package br.com.paulosedassari.votacao.domain.elegibilidade.port.outbound;

import br.com.paulosedassari.votacao.domain.elegibilidade.model.StatusElegibilidade;

import java.util.Optional;

public interface ValidadorElegibilidade {

    Optional<StatusElegibilidade> consultar(String cpf);
}
