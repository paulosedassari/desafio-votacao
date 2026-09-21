package br.com.paulosedassari.votacao.interfaces.web.voto;

import br.com.paulosedassari.votacao.domain.voto.port.inbound.RegistrarVotoUseCase;
import br.com.paulosedassari.votacao.interfaces.web.voto.dto.VotoRequest;
import br.com.paulosedassari.votacao.interfaces.web.voto.dto.VotoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VotoController implements VotoOpenApi {

    private final RegistrarVotoUseCase registrarVotoUseCase;

    @Override
    public ResponseEntity<VotoResponse> registrarVoto(Long pautaId, VotoRequest request) {
        var command = request.toCommand(pautaId);
        var voto = registrarVotoUseCase.executar(command);
        var response = VotoResponse.toResponse(voto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
