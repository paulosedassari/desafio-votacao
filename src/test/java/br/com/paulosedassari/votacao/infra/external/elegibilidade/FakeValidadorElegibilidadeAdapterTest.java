package br.com.paulosedassari.votacao.infra.external.elegibilidade;

import br.com.paulosedassari.votacao.domain.elegibilidade.model.StatusElegibilidade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FakeValidadorElegibilidadeAdapterTest {

    @Mock
    private RandomGenerator randomGenerator;

    @Test
    void deveRetornarAptoParaCpfValido() {
        when(randomGenerator.nextBoolean()).thenReturn(true);
        var adapter = new FakeValidadorElegibilidadeAdapter(randomGenerator);

        var resultado = adapter.consultar("529.982.247-25");

        assertThat(resultado).contains(StatusElegibilidade.ABLE_TO_VOTE);
    }

    @Test
    void deveRetornarNaoAptoParaCpfValido() {
        when(randomGenerator.nextBoolean()).thenReturn(false);
        var adapter = new FakeValidadorElegibilidadeAdapter(randomGenerator);

        var resultado = adapter.consultar("52998224725");

        assertThat(resultado).contains(StatusElegibilidade.UNABLE_TO_VOTE);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "",
            "52998224724",
            "11111111111",
            "529.982.247/25",
            "cpf52998224725"
    })
    void deveRejeitarCpfInvalido(String cpf) {
        var adapter = new FakeValidadorElegibilidadeAdapter(randomGenerator);

        var resultado = adapter.consultar(cpf);

        assertThat(resultado).isEmpty();
        verify(randomGenerator, never()).nextBoolean();
    }
}
