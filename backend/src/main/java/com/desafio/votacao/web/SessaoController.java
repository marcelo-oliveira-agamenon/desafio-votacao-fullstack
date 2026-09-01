package com.desafio.votacao.web;

import com.desafio.votacao.domain.SessaoVotacao;
import com.desafio.votacao.service.SessaoService;
import com.desafio.votacao.web.dto.AbrirSessaoRequest;
import com.desafio.votacao.web.dto.SessaoResponse;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/sessao")
public class SessaoController {

    private final SessaoService service;

    public SessaoController(SessaoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessaoResponse abrir(
        @PathVariable Long pautaId,
        @Valid @RequestBody(required = false) AbrirSessaoRequest request
    ) {
        Long duracaoSegundos = request == null ? null : request.duracaoSegundos();
        SessaoVotacao sessao = service.abrir(pautaId, duracaoSegundos);
        return SessaoResponse.from(sessao, Instant.now());
    }

    @GetMapping
    public SessaoResponse consultar(@PathVariable Long pautaId) {
        return SessaoResponse.from(service.buscarPorPauta(pautaId), Instant.now());
    }
}
