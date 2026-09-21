package br.com.paulosedassari.votacao.domain.voto.port.outbound;

import br.com.paulosedassari.votacao.domain.voto.model.Voto;

public interface VotoPersistencePort {

    boolean existePorPautaIdEAssociadoId(Long pautaId, String associadoId);

    Voto salvar(Voto voto);

    ContagemVotos contabilizarPorPautaId(Long pautaId);
}
