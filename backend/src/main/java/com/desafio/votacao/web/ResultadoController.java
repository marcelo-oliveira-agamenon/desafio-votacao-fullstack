package com.desafio.votacao.web;

import com.desafio.votacao.service.ResultadoService;
import com.desafio.votacao.web.dto.ResultadoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/resultado")
public class ResultadoController {

    private final ResultadoService service;

    public ResultadoController(ResultadoService service) {
        this.service = service;
    }

    @GetMapping
    public ResultadoResponse apurar(@PathVariable Long pautaId) {
        return service.apurar(pautaId);
    }
}
