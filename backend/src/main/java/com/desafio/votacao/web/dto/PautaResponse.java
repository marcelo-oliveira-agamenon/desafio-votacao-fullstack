package com.desafio.votacao.web.dto;

import com.desafio.votacao.domain.Pauta;
import java.time.Instant;

public record PautaResponse(Long id, String titulo, String descricao, Instant criadaEm) {

    public static PautaResponse from(Pauta pauta) {
        return new PautaResponse(pauta.getId(), pauta.getTitulo(), pauta.getDescricao(), pauta.getCriadaEm());
    }
}
