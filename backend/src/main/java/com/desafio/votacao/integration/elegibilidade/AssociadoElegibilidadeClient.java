package com.desafio.votacao.integration.elegibilidade;

public interface AssociadoElegibilidadeClient {

    /**
     * Consulta a elegibilidade de um associado pelo CPF.
     *
     * @throws com.desafio.votacao.exception.CpfInvalidoException quando o CPF não é reconhecido (HTTP 404)
     */
    ElegibilidadeResponse consultar(String cpf);
}
