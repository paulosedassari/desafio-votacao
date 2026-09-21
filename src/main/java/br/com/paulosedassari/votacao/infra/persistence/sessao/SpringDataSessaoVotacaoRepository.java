package br.com.paulosedassari.votacao.infra.persistence.sessao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface SpringDataSessaoVotacaoRepository extends JpaRepository<SessaoVotacaoJpaEntity, Long> {

	boolean existsByPautaId(Long pautaId);

	Optional<SessaoVotacaoJpaEntity> findByPautaId(Long pautaId);
}
