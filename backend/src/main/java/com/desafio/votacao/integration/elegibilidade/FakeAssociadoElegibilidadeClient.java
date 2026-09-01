package com.desafio.votacao.integration.elegibilidade;

import com.desafio.votacao.exception.CpfInvalidoException;
import com.desafio.votacao.support.CpfValidator;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Client fake da Tarefa Bônus 1: decide aleatoriamente se um CPF é
 * reconhecido e, quando é, se o associado pode ou não votar.
 */
@Component
public class FakeAssociadoElegibilidadeClient implements AssociadoElegibilidadeClient {

    private static final Logger log = LoggerFactory.getLogger(FakeAssociadoElegibilidadeClient.class);

    private final ElegibilidadeProperties properties;

    public FakeAssociadoElegibilidadeClient(ElegibilidadeProperties properties) {
        this.properties = properties;
    }

    @Override
    public ElegibilidadeResponse consultar(String cpf) {
        String normalizado = CpfValidator.normalizar(cpf);
        boolean naoEncontrado = !CpfValidator.isValido(normalizado)
            || sorteio(properties.probabilidadeCpfInvalido());
        if (naoEncontrado) {
            log.info("Elegibilidade: CPF {} não encontrado", normalizado);
            throw new CpfInvalidoException(normalizado);
        }

        StatusElegibilidade status = sorteio(properties.probabilidadeInelegivel())
            ? StatusElegibilidade.UNABLE_TO_VOTE
            : StatusElegibilidade.ABLE_TO_VOTE;
        log.info("Elegibilidade: CPF {} -> {}", normalizado, status);
        return new ElegibilidadeResponse(status);
    }

    private static boolean sorteio(double probabilidade) {
        return probabilidade > 0 && ThreadLocalRandom.current().nextDouble() < probabilidade;
    }
}
