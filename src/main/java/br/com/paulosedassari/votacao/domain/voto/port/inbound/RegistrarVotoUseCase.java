package br.com.paulosedassari.votacao.domain.voto.port.inbound;

public interface RegistrarVotoUseCase {

    VotoRegistrado executar(RegistrarVotoCommand command);
}
