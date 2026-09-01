package com.desafio.votacao.web;

import com.desafio.votacao.domain.Pauta;
import com.desafio.votacao.service.PautaService;
import com.desafio.votacao.web.dto.CriarPautaRequest;
import com.desafio.votacao.web.dto.PautaResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {

    private final PautaService service;

    public PautaController(PautaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody CriarPautaRequest request) {
        Pauta pauta = service.criar(request.titulo(), request.descricao());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(pauta.getId())
            .toUri();
        return ResponseEntity.created(location).body(PautaResponse.from(pauta));
    }

    @GetMapping
    public List<PautaResponse> listar() {
        return service.listar().stream().map(PautaResponse::from).toList();
    }
}
