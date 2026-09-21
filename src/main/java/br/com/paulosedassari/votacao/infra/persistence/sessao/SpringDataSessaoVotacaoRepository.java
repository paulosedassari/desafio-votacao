package br.com.paulosedassari.votacao.infra.persistence.sessao;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataSessaoVotacaoRepository extends JpaRepository<SessaoVotacaoJpaEntity, Long> {

	boolean existsByPautaId(Long pautaId);
}
