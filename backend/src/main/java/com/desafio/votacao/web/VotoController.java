package com.desafio.votacao.web;

import com.desafio.votacao.domain.Voto;
import com.desafio.votacao.service.VotoService;
import com.desafio.votacao.web.dto.RegistrarVotoRequest;
import com.desafio.votacao.web.dto.VotoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
public class VotoController {

    private final VotoService service;

    public VotoController(VotoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VotoResponse registrar(
        @PathVariable Long pautaId,
        @Valid @RequestBody RegistrarVotoRequest request
    ) {
        Voto voto = service.registrar(pautaId, request.associadoId(), request.escolha());
        return VotoResponse.from(voto);
    }
}
