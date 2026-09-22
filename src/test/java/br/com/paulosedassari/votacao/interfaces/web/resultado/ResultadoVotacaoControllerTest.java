package br.com.paulosedassari.votacao.interfaces.web.resultado;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.paulosedassari.votacao.domain.pauta.exception.PautaNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoEmAndamentoException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.ConsultarResultadoVotacaoUseCase;
import br.com.paulosedassari.votacao.domain.voto.port.inbound.ResultadoVotacao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ResultadoVotacaoController.class)
class ResultadoVotacaoControllerTest {

    @MockitoBean
    private ConsultarResultadoVotacaoUseCase useCase;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarResultado() throws Exception {
        when(useCase.executar(42L)).thenReturn(new ResultadoVotacao(42L, 150, 83, 233));

        mockMvc.perform(get("/api/v1/pautas/42/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId").value(42))
                .andExpect(jsonPath("$.sim").value(150))
                .andExpect(jsonPath("$.nao").value(83))
                .andExpect(jsonPath("$.total").value(233));
    }

    @Test
    void deveRetornarNaoEncontradoParaPautaInexistente() throws Exception {
        when(useCase.executar(42L)).thenThrow(new PautaNaoEncontradaException(42L));

        mockMvc.perform(get("/api/v1/pautas/42/resultado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("PAUTA_NAO_ENCONTRADA"));
    }

    @Test
    void deveRetornarNaoEncontradoParaSessaoInexistente() throws Exception {
        when(useCase.executar(42L)).thenThrow(new SessaoNaoEncontradaException(42L));

        mockMvc.perform(get("/api/v1/pautas/42/resultado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("SESSAO_NAO_ENCONTRADA"));
    }

    @Test
    void deveRetornarConflitoEnquantoSessaoEstiverAberta() throws Exception {
        when(useCase.executar(42L)).thenThrow(new SessaoEmAndamentoException(42L));

        mockMvc.perform(get("/api/v1/pautas/42/resultado"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("SESSAO_EM_ANDAMENTO"));
    }
}
