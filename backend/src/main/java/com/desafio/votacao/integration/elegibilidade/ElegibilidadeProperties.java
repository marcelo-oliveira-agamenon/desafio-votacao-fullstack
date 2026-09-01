package com.desafio.votacao.integration.elegibilidade;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Probabilidades usadas pelo client fake. Valores ausentes assumem 0.2;
 * defina 0 para desligar (útil para testes manuais determinísticos).
 */
@ConfigurationProperties(prefix = "votacao.elegibilidade")
public record ElegibilidadeProperties(Double probabilidadeCpfInvalido, Double probabilidadeInelegivel) {

    public ElegibilidadeProperties {
        probabilidadeCpfInvalido = normalizar(probabilidadeCpfInvalido);
        probabilidadeInelegivel = normalizar(probabilidadeInelegivel);
    }

    private static double normalizar(Double valor) {
        if (valor == null) {
            return 0.2;
        }
        return Math.clamp(valor, 0.0, 1.0);
    }
}
