package com.desafio.votacao.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarPautaRequest(
    @NotBlank @Size(max = 200) String titulo,
    @Size(max = 2000) String descricao
) {
}
