package br.com.paulosedassari.votacao.interfaces.web.elegibilidade;

import br.com.paulosedassari.votacao.domain.elegibilidade.port.inbound.ConsultarElegibilidadeUseCase;
import br.com.paulosedassari.votacao.interfaces.web.elegibilidade.dto.ElegibilidadeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ElegibilidadeController implements ElegibilidadeOpenApi {

    private final ConsultarElegibilidadeUseCase consultarElegibilidadeUseCase;

    @Override
    public ResponseEntity<ElegibilidadeResponse> consultar(String cpf) {
        var elegibilidade = consultarElegibilidadeUseCase.executar(cpf);
        var response = ElegibilidadeResponse.toResponse(elegibilidade);

        return ResponseEntity.ok(response);
    }
}
