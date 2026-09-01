package com.desafio.votacao.web.dto;

import com.desafio.votacao.domain.Desfecho;

public record ResultadoResponse(
    Long pautaId,
    boolean sessaoEncerrada,
    long votosSim,
    long votosNao,
    long totalVotos,
    Desfecho desfecho
) {

    public static ResultadoResponse of(Long pautaId, boolean sessaoEncerrada, long votosSim, long votosNao) {
        Desfecho desfecho = votosSim > votosNao
            ? Desfecho.APROVADA
            : votosNao > votosSim ? Desfecho.REJEITADA : Desfecho.EMPATE;
        return new ResultadoResponse(pautaId, sessaoEncerrada, votosSim, votosNao, votosSim + votosNao, desfecho);
    }
}
