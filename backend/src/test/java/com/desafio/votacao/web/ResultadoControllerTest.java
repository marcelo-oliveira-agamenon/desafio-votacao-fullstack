package com.desafio.votacao.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.desafio.votacao.exception.RecursoNaoEncontradoException;
import com.desafio.votacao.service.ResultadoService;
import com.desafio.votacao.web.dto.ResultadoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ResultadoController.class)
class ResultadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResultadoService service;

    @Test
    void retornaApuracao() throws Exception {
        when(service.apurar(1L)).thenReturn(ResultadoResponse.of(1L, true, 5, 2));

        mockMvc.perform(get("/api/v1/pautas/1/resultado"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.votosSim").value(5))
            .andExpect(jsonPath("$.totalVotos").value(7))
            .andExpect(jsonPath("$.desfecho").value("APROVADA"));
    }

    @Test
    void semSessaoRetorna404() throws Exception {
        when(service.apurar(1L)).thenThrow(new RecursoNaoEncontradoException("sem sessão"));

        mockMvc.perform(get("/api/v1/pautas/1/resultado"))
            .andExpect(status().isNotFound());
    }
}
