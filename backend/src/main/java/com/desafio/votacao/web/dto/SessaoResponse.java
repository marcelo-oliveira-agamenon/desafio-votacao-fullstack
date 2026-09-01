package com.desafio.votacao.web.dto;

import com.desafio.votacao.domain.SessaoVotacao;
import java.time.Instant;

public record SessaoResponse(
    Long id,
    Long pautaId,
    Instant abertura,
    Instant encerramento,
    boolean aberta
) {

    public static SessaoResponse from(SessaoVotacao sessao, Instant momento) {
        return new SessaoResponse(
            sessao.getId(),
            sessao.getPauta().getId(),
            sessao.getAbertura(),
            sessao.getEncerramento(),
            sessao.estaAberta(momento)
        );
    }
}
