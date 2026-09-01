package com.desafio.votacao.exception;

public class CpfInvalidoException extends RecursoNaoEncontradoException {

    public CpfInvalidoException(String cpf) {
        super("CPF " + cpf + " não encontrado ou inválido");
    }
}
