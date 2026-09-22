package br.com.paulosedassari.votacao.domain.pauta.port.outbound;

import br.com.paulosedassari.votacao.domain.pauta.model.Pauta;

import java.util.List;

public interface PautaPersistencePort {

	Pauta salvar(Pauta pauta);

	boolean existePorId(Long pautaId);

	List<Pauta> buscarTodas();
}
