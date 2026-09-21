package br.com.paulosedassari.votacao.interfaces.web.sessao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.paulosedassari.votacao.domain.sessao.port.inbound.AbrirSessaoVotacaoUseCase;
import br.com.paulosedassari.votacao.interfaces.web.sessao.dto.SessaoRequest;
import br.com.paulosedassari.votacao.interfaces.web.sessao.dto.SessaoResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SessaoVotacaoController implements SessaoVotacaoOpenApi {

	private final AbrirSessaoVotacaoUseCase abrirSessaoVotacaoUseCase;

	@Override
	public ResponseEntity<SessaoResponse> abrirSessao(
			Long pautaId,
			SessaoRequest request
	) {
		var command = request.toCommand(pautaId);
		var sessao = abrirSessaoVotacaoUseCase.executar(command);
		var response = SessaoResponse.toResponse(sessao);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
