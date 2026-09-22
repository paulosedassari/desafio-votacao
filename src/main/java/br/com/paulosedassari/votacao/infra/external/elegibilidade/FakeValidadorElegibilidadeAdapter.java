package br.com.paulosedassari.votacao.infra.external.elegibilidade;

import br.com.paulosedassari.votacao.domain.elegibilidade.model.StatusElegibilidade;
import br.com.paulosedassari.votacao.domain.elegibilidade.port.outbound.ValidadorElegibilidade;

import java.util.Optional;
import java.util.random.RandomGenerator;

public class FakeValidadorElegibilidadeAdapter implements ValidadorElegibilidade {

    private static final int TAMANHO_CPF = 11;

    private final RandomGenerator randomGenerator;

    public FakeValidadorElegibilidadeAdapter(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    @Override
    public Optional<StatusElegibilidade> consultar(String cpf) {
        if (cpf == null || !formatoValido(cpf)) {
            return Optional.empty();
        }

        String cpfNormalizado = normalizar(cpf);

        if (!valido(cpfNormalizado)) {
            return Optional.empty();
        }

        StatusElegibilidade status = randomGenerator.nextBoolean()
                ? StatusElegibilidade.ABLE_TO_VOTE
                : StatusElegibilidade.UNABLE_TO_VOTE;

        return Optional.of(status);
    }

    private String normalizar(String cpf) {
        return cpf.replace(".", "").replace("-", "");
    }

    private boolean formatoValido(String cpf) {
        return cpf.matches("\\d{11}") || cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }

    private boolean valido(String cpf) {
        if (cpf.length() != TAMANHO_CPF || cpf.chars().distinct().count() == 1) {
            return false;
        }

        return calcularDigito(cpf, 9) == Character.getNumericValue(cpf.charAt(9))
                && calcularDigito(cpf, 10) == Character.getNumericValue(cpf.charAt(10));
    }

    private int calcularDigito(String cpf, int quantidadeDigitos) {
        int soma = 0;

        for (int indice = 0; indice < quantidadeDigitos; indice++) {
            soma += Character.getNumericValue(cpf.charAt(indice)) * (quantidadeDigitos + 1 - indice);
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
