package br.com.paulosedassari.votacao.infra.persistence.voto;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataVotoRepository extends JpaRepository<VotoJpaEntity, Long> {

    boolean existsByPautaIdAndAssociadoId(Long pautaId, String associadoId);
}
