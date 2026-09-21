package br.com.paulosedassari.votacao.domain.pauta.port.inbound;

public interface CadastrarPautaUseCase {

	PautaCriada executar(CadastrarPautaCommand command);
}
