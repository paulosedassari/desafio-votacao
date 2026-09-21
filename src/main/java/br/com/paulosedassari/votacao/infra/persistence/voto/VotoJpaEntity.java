package br.com.paulosedassari.votacao.infra.persistence.voto;

import br.com.paulosedassari.votacao.domain.voto.model.ValorVoto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "voto")
class VotoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pauta_id", nullable = false)
    private Long pautaId;

    @Column(name = "associado_id", nullable = false, length = 100)
    private String associadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private ValorVoto valor;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected VotoJpaEntity() {
    }

    VotoJpaEntity(
            Long id,
            Long pautaId,
            String associadoId,
            ValorVoto valor,
            Instant criadoEm
    ) {
        this.id = id;
        this.pautaId = pautaId;
        this.associadoId = associadoId;
        this.valor = valor;
        this.criadoEm = criadoEm;
    }
}
