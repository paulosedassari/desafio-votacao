package br.com.paulosedassari.votacao.infra.persistence.voto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface SpringDataVotoRepository extends JpaRepository<VotoJpaEntity, Long> {

    boolean existsByPautaIdAndAssociadoId(Long pautaId, String associadoId);

    @Query(value = """
            SELECT valor AS valor, COUNT(*) AS quantidade
            FROM voto
            WHERE pauta_id = :pautaId
            GROUP BY valor
            """, nativeQuery = true)
    List<ContagemVotosProjection> contabilizarPorPautaId(@Param("pautaId") Long pautaId);
}
