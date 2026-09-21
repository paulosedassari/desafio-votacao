package br.com.paulosedassari.votacao.infra.persistence.pauta;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataPautaRepository extends JpaRepository<PautaJpaEntity, Long> {
}
