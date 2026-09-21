package br.com.paulosedassari.votacao.infra.persistence.sessao;

import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoJaExistenteException;
import br.com.paulosedassari.votacao.domain.sessao.model.SessaoVotacao;
import br.com.paulosedassari.votacao.domain.sessao.port.outbound.SessaoVotacaoPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
class SessaoVotacaoPersistenceAdapter implements SessaoVotacaoPersistencePort {

    private final SpringDataSessaoVotacaoRepository repository;

    @Override
    public boolean existePorPautaId(Long pautaId) {
        return repository.existsByPautaId(pautaId);
    }

    @Override
    public Optional<SessaoVotacao> buscarPorPautaId(Long pautaId) {
        return repository.findByPautaId(pautaId)
                .map(entidade -> new SessaoVotacao(
                        entidade.getId(),
                        entidade.getPautaId(),
                        entidade.getAbertaEm(),
                        entidade.getEncerraEm()
                ));
    }

    @Override
    public SessaoVotacao salvar(SessaoVotacao sessao) {
        try {
            var entidade = new SessaoVotacaoJpaEntity(
                    sessao.id(),
                    sessao.pautaId(),
                    sessao.abertaEm(),
                    sessao.encerraEm()
            );

            SessaoVotacaoJpaEntity entidadeSalva = repository.saveAndFlush(entidade);

            log.info("Sessão aberta. sessaoId={} pautaId={}", entidadeSalva.getId(), sessao.pautaId());

            return new SessaoVotacao(
                    entidadeSalva.getId(),
                    entidadeSalva.getPautaId(),
                    entidadeSalva.getAbertaEm(),
                    entidadeSalva.getEncerraEm()
            );
        } catch (DataIntegrityViolationException exception) {
            throw new SessaoJaExistenteException(sessao.pautaId());
        }
    }
}
