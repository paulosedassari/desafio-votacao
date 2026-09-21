package br.com.paulosedassari.votacao.interfaces.web.resultado;

import br.com.paulosedassari.votacao.domain.voto.port.inbound.ConsultarResultadoVotacaoUseCase;
import br.com.paulosedassari.votacao.interfaces.web.resultado.dto.ResultadoVotacaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResultadoVotacaoController implements ResultadoVotacaoOpenApi {

    private final ConsultarResultadoVotacaoUseCase consultarResultadoVotacaoUseCase;

    @Override
    public ResponseEntity<ResultadoVotacaoResponse> consultarResultado(Long pautaId) {
        var resultado = consultarResultadoVotacaoUseCase.executar(pautaId);
        var response = ResultadoVotacaoResponse.toResponse(resultado);

        return ResponseEntity.ok(response);
    }
}
