package br.com.paulosedassari.votacao.interfaces.web.pauta;

import br.com.paulosedassari.votacao.domain.pauta.port.inbound.CadastrarPautaUseCase;
import br.com.paulosedassari.votacao.interfaces.web.pauta.dto.PautaRequest;
import br.com.paulosedassari.votacao.interfaces.web.pauta.dto.PautaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PautaController implements PautaOpenApi {

    private final CadastrarPautaUseCase cadastrarPautaUseCase;

    @Override
    public ResponseEntity<PautaResponse> criarPauta(PautaRequest pautaRequest) {
        var request = pautaRequest.toCommand();
        var pautaCriada = cadastrarPautaUseCase.executar(request);
        var response = PautaResponse.toResponse(pautaCriada);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
