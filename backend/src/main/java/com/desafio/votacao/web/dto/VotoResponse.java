package com.desafio.votacao.web.dto;

import com.desafio.votacao.domain.EscolhaVoto;
import com.desafio.votacao.domain.Voto;
import java.time.Instant;

public record VotoResponse(
    Long id,
    Long pautaId,
    String associadoId,
    EscolhaVoto escolha,
    Instant registradoEm
) {

    public static VotoResponse from(Voto voto) {
        return new VotoResponse(
            voto.getId(),
            voto.getSessao().getPauta().getId(),
            voto.getAssociadoId(),
            voto.getEscolha(),
            voto.getRegistradoEm()
        );
    }
}
