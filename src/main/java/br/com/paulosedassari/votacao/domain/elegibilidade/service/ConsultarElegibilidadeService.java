package br.com.paulosedassari.votacao.domain.elegibilidade.service;

import br.com.paulosedassari.votacao.domain.elegibilidade.exception.CpfInvalidoException;
import br.com.paulosedassari.votacao.domain.elegibilidade.port.inbound.ConsultarElegibilidadeUseCase;
import br.com.paulosedassari.votacao.domain.elegibilidade.port.inbound.ElegibilidadeConsultada;
import br.com.paulosedassari.votacao.domain.elegibilidade.port.outbound.ValidadorElegibilidade;

public class ConsultarElegibilidadeService implements ConsultarElegibilidadeUseCase {

    private final ValidadorElegibilidade validadorElegibilidade;

    public ConsultarElegibilidadeService(ValidadorElegibilidade validadorElegibilidade) {
        this.validadorElegibilidade = validadorElegibilidade;
    }

    @Override
    public ElegibilidadeConsultada executar(String cpf) {
        return validadorElegibilidade.consultar(cpf)
                .map(ElegibilidadeConsultada::new)
                .orElseThrow(CpfInvalidoException::new);
    }
}
