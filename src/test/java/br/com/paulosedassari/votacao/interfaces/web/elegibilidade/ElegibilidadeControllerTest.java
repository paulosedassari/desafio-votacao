package br.com.paulosedassari.votacao.interfaces.web.elegibilidade;

import br.com.paulosedassari.votacao.domain.elegibilidade.exception.CpfInvalidoException;
import br.com.paulosedassari.votacao.domain.elegibilidade.model.StatusElegibilidade;
import br.com.paulosedassari.votacao.domain.elegibilidade.port.inbound.ConsultarElegibilidadeUseCase;
import br.com.paulosedassari.votacao.domain.elegibilidade.port.inbound.ElegibilidadeConsultada;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ElegibilidadeController.class)
class ElegibilidadeControllerTest {

    private static final String CPF = "52998224725";

    @MockitoBean
    private ConsultarElegibilidadeUseCase useCase;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveConsultarElegibilidade() throws Exception {
        when(useCase.executar(CPF))
                .thenReturn(new ElegibilidadeConsultada(StatusElegibilidade.ABLE_TO_VOTE));

        mockMvc.perform(get("/api/v1/elegibilidade").param("cpf", CPF))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABLE_TO_VOTE"));
    }

    @Test
    void deveRetornarNaoEncontradoParaCpfInvalido() throws Exception {
        when(useCase.executar(CPF)).thenThrow(new CpfInvalidoException());

        mockMvc.perform(get("/api/v1/elegibilidade").param("cpf", CPF))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("CPF_INVALIDO"))
                .andExpect(jsonPath("$.mensagem").value("CPF inválido"))
                .andExpect(jsonPath("$.caminho").value("/api/v1/elegibilidade"));
    }

    @Test
    void deveRejeitarConsultaSemCpf() throws Exception {
        mockMvc.perform(get("/api/v1/elegibilidade"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("PARAMETRO_OBRIGATORIO"));

        verify(useCase, never()).executar(org.mockito.ArgumentMatchers.any());
    }
}
