package br.com.paulosedassari.votacao.infra.persistence.voto;

import br.com.paulosedassari.votacao.domain.voto.exception.VotoDuplicadoException;
import br.com.paulosedassari.votacao.domain.voto.model.ValorVoto;
import br.com.paulosedassari.votacao.domain.voto.model.Voto;
import br.com.paulosedassari.votacao.domain.voto.port.outbound.ContagemVotos;
import br.com.paulosedassari.votacao.domain.voto.port.outbound.VotoPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class VotoPersistenceAdapter implements VotoPersistencePort {

    private final SpringDataVotoRepository repository;

    @Override
    public boolean existePorPautaIdEAssociadoId(Long pautaId, String associadoId) {
        return repository.existsByPautaIdAndAssociadoId(pautaId, associadoId);
    }

    @Override
    public Voto salvar(Voto voto) {
        try {
            var entidade = new VotoJpaEntity(
                    voto.id(),
                    voto.pautaId(),
                    voto.associadoId(),
                    voto.valor(),
                    voto.criadoEm()
            );

            var entidadeSalva = repository.saveAndFlush(entidade);
            log.debug("Voto registrado. votoId={} pautaId={}", entidadeSalva.getId(), voto.pautaId());

            return new Voto(
                    entidadeSalva.getId(),
                    entidadeSalva.getPautaId(),
                    entidadeSalva.getAssociadoId(),
                    entidadeSalva.getValor(),
                    entidadeSalva.getCriadoEm()
            );
        } catch (DataIntegrityViolationException exception) {
            throw new VotoDuplicadoException();
        }
    }

    @Override
    public ContagemVotos contabilizarPorPautaId(Long pautaId) {
        long sim = 0;
        long nao = 0;

        for (var contagem : repository.contabilizarPorPautaId(pautaId)) {
            switch (contagem.getValor()) {
                case SIM -> sim = contagem.getQuantidade();
                case NAO -> nao = contagem.getQuantidade();
            }
        }

        return new ContagemVotos(sim, nao);
    }
}
