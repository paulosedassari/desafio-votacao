package br.com.paulosedassari.votacao.domain.elegibilidade.port.inbound;

public interface ConsultarElegibilidadeUseCase {

    ElegibilidadeConsultada executar(String cpf);
}
