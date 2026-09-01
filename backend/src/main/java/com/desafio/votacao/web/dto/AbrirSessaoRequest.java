package com.desafio.votacao.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

public record AbrirSessaoRequest(
    @Positive @Max(86_400) Long duracaoSegundos
) {
}
