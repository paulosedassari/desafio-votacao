package br.com.paulosedassari.votacao.domain.pauta.port.inbound;

import java.util.List;

public interface ListarPautasUseCase {

    List<PautaListada> executar();
}
