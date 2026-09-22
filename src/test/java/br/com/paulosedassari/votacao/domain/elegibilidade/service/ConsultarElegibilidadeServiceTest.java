package br.com.paulosedassari.votacao.domain.elegibilidade.service;

import br.com.paulosedassari.votacao.domain.elegibilidade.exception.CpfInvalidoException;
import br.com.paulosedassari.votacao.domain.elegibilidade.model.StatusElegibilidade;
import br.com.paulosedassari.votacao.domain.elegibilidade.port.outbound.ValidadorElegibilidade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarElegibilidadeServiceTest {

    private static final String CPF = "52998224725";

    @Mock
    private ValidadorElegibilidade validadorElegibilidade;

    @ParameterizedTest
    @EnumSource(StatusElegibilidade.class)
    void deveConsultarElegibilidade(StatusElegibilidade status) {
        when(validadorElegibilidade.consultar(CPF)).thenReturn(Optional.of(status));
        var service = new ConsultarElegibilidadeService(validadorElegibilidade);

        var resultado = service.executar(CPF);

        assertThat(resultado.status()).isEqualTo(status);
    }

    @Test
    void deveRejeitarCpfInvalido() {
        when(validadorElegibilidade.consultar(CPF)).thenReturn(Optional.empty());
        var service = new ConsultarElegibilidadeService(validadorElegibilidade);

        assertThatThrownBy(() -> service.executar(CPF))
                .isInstanceOf(CpfInvalidoException.class)
                .hasMessage("CPF inválido");
    }
}
