package br.com.paulosedassari.votacao.infra.persistence.sessao;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "sessao_votacao")
class SessaoVotacaoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pauta_id", nullable = false, unique = true)
    private Long pautaId;

    @Column(name = "aberta_em", nullable = false)
    private Instant abertaEm;

    @Column(name = "encerra_em", nullable = false)
    private Instant encerraEm;

    SessaoVotacaoJpaEntity(
            Long id,
            Long pautaId,
            Instant abertaEm,
            Instant encerraEm
    ) {
        this.id = id;
        this.pautaId = pautaId;
        this.abertaEm = abertaEm;
        this.encerraEm = encerraEm;
    }
}
