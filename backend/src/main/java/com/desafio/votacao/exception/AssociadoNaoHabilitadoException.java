package com.desafio.votacao.exception;

public class AssociadoNaoHabilitadoException extends RecursoNaoEncontradoException {

    public AssociadoNaoHabilitadoException(String cpf) {
        super("Associado com CPF " + cpf + " não está habilitado a votar");
    }
}
