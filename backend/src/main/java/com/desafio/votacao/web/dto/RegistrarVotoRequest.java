package com.desafio.votacao.web.dto;

import com.desafio.votacao.domain.EscolhaVoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarVotoRequest(
    @NotBlank @Size(max = 64) String associadoId,
    @NotNull EscolhaVoto escolha
) {
}
