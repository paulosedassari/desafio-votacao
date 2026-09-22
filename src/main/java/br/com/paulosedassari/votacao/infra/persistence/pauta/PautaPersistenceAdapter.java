package br.com.paulosedassari.votacao.infra.persistence.pauta;

import br.com.paulosedassari.votacao.domain.pauta.model.Pauta;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
class PautaPersistenceAdapter implements PautaPersistencePort {

    private final SpringDataPautaRepository repository;

    @Override
    public Pauta salvar(Pauta pauta) {
        var entidade = new PautaJpaEntity(
                pauta.id(),
                pauta.titulo(),
                pauta.descricao(),
                pauta.criadoEm()
        );

        var entidadeCriada = repository.save(entidade);
        log.info("Pauta cadastrada. pautaId={}", entidadeCriada.getId());

        return new Pauta(
                entidadeCriada.getId(),
                entidadeCriada.getTitulo(),
                entidadeCriada.getDescricao(),
                entidadeCriada.getCriadoEm()
        );
    }

    @Override
    public boolean existePorId(Long pautaId) {
        return repository.existsById(pautaId);
    }

    @Override
    public List<Pauta> buscarTodas() {
        return repository.findAll().stream()
                .map(entidade -> new Pauta(
                        entidade.getId(),
                        entidade.getTitulo(),
                        entidade.getDescricao(),
                        entidade.getCriadoEm()
                ))
                .toList();
    }
}
