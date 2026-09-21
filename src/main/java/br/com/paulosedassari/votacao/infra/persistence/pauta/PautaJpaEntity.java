package br.com.paulosedassari.votacao.infra.persistence.pauta;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "pauta")
class PautaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected PautaJpaEntity() {
    }

    PautaJpaEntity(
            Long id,
            String titulo,
            String descricao,
            Instant criadoEm
    ) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.criadoEm = criadoEm;
    }
}
