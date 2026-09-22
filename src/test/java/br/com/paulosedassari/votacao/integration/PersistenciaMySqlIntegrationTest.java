package br.com.paulosedassari.votacao.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.paulosedassari.votacao.domain.pauta.model.Pauta;
import br.com.paulosedassari.votacao.domain.pauta.port.outbound.PautaPersistencePort;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoJaExistenteException;
import br.com.paulosedassari.votacao.domain.sessao.model.SessaoVotacao;
import br.com.paulosedassari.votacao.domain.sessao.port.outbound.SessaoVotacaoPersistencePort;
import br.com.paulosedassari.votacao.domain.voto.exception.VotoDuplicadoException;
import br.com.paulosedassari.votacao.domain.voto.model.ValorVoto;
import br.com.paulosedassari.votacao.domain.voto.model.Voto;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.RegistrarVotoCommand;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.RegistrarVotoUseCase;
import br.com.paulosedassari.votacao.domain.voto.port.outbound.ContagemVotos;
import br.com.paulosedassari.votacao.domain.voto.port.outbound.VotoPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Testcontainers
@SpringBootTest
class PersistenciaMySqlIntegrationTest {

    private static final int QUANTIDADE_REQUISICOES_CONCORRENTES = 8;

    @Container
    @ServiceConnection
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4")
            .withDatabaseName("votacao")
            .withUsername("votacao")
            .withPassword("votacao");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PautaPersistencePort pautaPersistencePort;

    @Autowired
    private SessaoVotacaoPersistencePort sessaoPersistencePort;

    @Autowired
    private VotoPersistencePort votoPersistencePort;

    @Autowired
    private RegistrarVotoUseCase registrarVotoUseCase;

    @BeforeEach
    void limparBanco() {
        jdbcTemplate.update("DELETE FROM voto");
        jdbcTemplate.update("DELETE FROM sessao_votacao");
        jdbcTemplate.update("DELETE FROM pauta");
    }

    @Test
    void deveExecutarMigrationsComConstraintsEIndiceEsperados() {
        var migrationsExecutadas = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = 1",
                Integer.class
        );
        var constraints = jdbcTemplate.queryForList("""
                SELECT constraint_name
                FROM information_schema.table_constraints
                WHERE constraint_schema = DATABASE()
                  AND constraint_name IN ('uk_sessao_votacao_pauta', 'uk_voto_pauta_associado')
                """, String.class);
        var indiceVoto = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = 'voto'
                  AND index_name = 'idx_voto_pauta'
                """, Integer.class);

        assertThat(migrationsExecutadas).isEqualTo(1);
        assertThat(constraints).containsExactlyInAnyOrder(
                "uk_sessao_votacao_pauta",
                "uk_voto_pauta_associado"
        );
        assertThat(indiceVoto).isEqualTo(1);
    }

    @Test
    void devePersistirEListarPautas() {
        var primeira = criarPauta("Primeira pauta");
        var segunda = criarPauta("Segunda pauta");

        var pautas = pautaPersistencePort.buscarTodas();

        assertThat(pautas).extracting(Pauta::id).containsExactly(primeira.id(), segunda.id());
        assertThat(pautas).extracting(Pauta::titulo).containsExactly("Primeira pauta", "Segunda pauta");
    }

    @Test
    void deveGarantirApenasUmaSessaoPorPauta() {
        var pauta = criarPauta("Sessão única");
        var agora = Instant.now();
        sessaoPersistencePort.salvar(SessaoVotacao.nova(
                pauta.id(),
                agora,
                agora.plusSeconds(60)
        ));

        assertThatThrownBy(() -> sessaoPersistencePort.salvar(SessaoVotacao.nova(
                pauta.id(),
                agora.plusSeconds(1),
                agora.plusSeconds(61)
        ))).isInstanceOf(SessaoJaExistenteException.class);

        assertThat(contar("sessao_votacao")).isEqualTo(1);
    }

    @Test
    void deveGarantirApenasUmVotoPorAssociadoEPauta() {
        var pauta = criarPauta("Voto único");
        var agora = Instant.now();
        votoPersistencePort.salvar(Voto.novo(pauta.id(), "12345", ValorVoto.SIM, agora));

        assertThatThrownBy(() -> votoPersistencePort.salvar(
                Voto.novo(pauta.id(), "12345", ValorVoto.NAO, agora.plusSeconds(1))
        )).isInstanceOf(VotoDuplicadoException.class);

        assertThat(contar("voto")).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("contagensDeVotos")
    void deveContabilizarVotosNoBanco(int quantidadeSim, int quantidadeNao) {
        var pauta = criarPauta("Contabilização");
        var agora = Instant.now();
        for (int indice = 0; indice < quantidadeSim; indice++) {
            salvarVoto(pauta.id(), "sim-" + indice, ValorVoto.SIM, agora);
        }
        for (int indice = 0; indice < quantidadeNao; indice++) {
            salvarVoto(pauta.id(), "nao-" + indice, ValorVoto.NAO, agora);
        }

        var resultado = votoPersistencePort.contabilizarPorPautaId(pauta.id());

        assertThat(resultado).isEqualTo(new ContagemVotos(quantidadeSim, quantidadeNao));
        assertThat(resultado.total()).isEqualTo(quantidadeSim + quantidadeNao);
    }

    @Test
    @Timeout(30)
    void devePersistirSomenteUmVotoEmRequisicoesConcorrentes() throws Exception {
        var pauta = criarPauta("Concorrência");
        var agora = Instant.now();
        sessaoPersistencePort.salvar(SessaoVotacao.nova(
                pauta.id(),
                agora.minusSeconds(10),
                agora.plusSeconds(300)
        ));
        var prontos = new CountDownLatch(QUANTIDADE_REQUISICOES_CONCORRENTES);
        var iniciar = new CountDownLatch(1);

        try (var executor = Executors.newFixedThreadPool(QUANTIDADE_REQUISICOES_CONCORRENTES)) {
            var resultados = new ArrayList<java.util.concurrent.Future<String>>();
            for (int indice = 0; indice < QUANTIDADE_REQUISICOES_CONCORRENTES; indice++) {
                resultados.add(executor.submit(() -> registrarConcorrentemente(pauta.id(), prontos, iniciar)));
            }

            assertThat(prontos.await(10, TimeUnit.SECONDS)).isTrue();
            iniciar.countDown();

            var respostas = new ArrayList<String>();
            for (var resultado : resultados) {
                respostas.add(resultado.get(10, TimeUnit.SECONDS));
            }

            assertThat(respostas).filteredOn("CRIADO"::equals).hasSize(1);
            assertThat(respostas).filteredOn("DUPLICADO"::equals)
                    .hasSize(QUANTIDADE_REQUISICOES_CONCORRENTES - 1);
        }

        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM voto WHERE pauta_id = ? AND associado_id = ?",
                Integer.class,
                pauta.id(),
                "associado-concorrente"
        )).isEqualTo(1);
    }

    private String registrarConcorrentemente(
            Long pautaId,
            CountDownLatch prontos,
            CountDownLatch iniciar
    ) throws InterruptedException {
        prontos.countDown();
        iniciar.await();

        try {
            registrarVotoUseCase.executar(new RegistrarVotoCommand(
                    pautaId,
                    "associado-concorrente",
                    ValorVoto.SIM
            ));
            return "CRIADO";
        } catch (VotoDuplicadoException exception) {
            return "DUPLICADO";
        }
    }

    private Pauta criarPauta(String titulo) {
        return pautaPersistencePort.salvar(Pauta.novaPauta(
                titulo,
                "Descrição da pauta",
                Instant.now()
        ));
    }

    private void salvarVoto(Long pautaId, String associadoId, ValorVoto valor, Instant criadoEm) {
        votoPersistencePort.salvar(Voto.novo(pautaId, associadoId, valor, criadoEm));
    }

    private int contar(String tabela) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tabela, Integer.class);
    }

    private static Stream<Arguments> contagensDeVotos() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(4, 0),
                Arguments.of(0, 3),
                Arguments.of(5, 5),
                Arguments.of(15, 8)
        );
    }
}
