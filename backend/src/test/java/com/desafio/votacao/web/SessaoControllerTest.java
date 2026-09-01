package com.desafio.votacao.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.desafio.votacao.domain.Pauta;
import com.desafio.votacao.domain.SessaoVotacao;
import com.desafio.votacao.exception.RegraDeNegocioException;
import com.desafio.votacao.service.SessaoService;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@WebMvcTest(SessaoController.class)
class SessaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessaoService service;

    private static SessaoVotacao sessao() {
        Pauta pauta = new Pauta("t", null);
        ReflectionTestUtils.setField(pauta, "id", 1L);
        SessaoVotacao sessao = new SessaoVotacao(pauta, Instant.now(), Duration.ofMinutes(1));
        ReflectionTestUtils.setField(sessao, "id", 10L);
        return sessao;
    }

    @Test
    void abreSessaoSemCorpoUsaDuracaoPadrao() throws Exception {
        when(service.abrir(eq(1L), isNull())).thenReturn(sessao());

        mockMvc.perform(post("/api/v1/pautas/1/sessao"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.pautaId").value(1))
            .andExpect(jsonPath("$.aberta").value(true));

        verify(service).abrir(1L, null);
    }

    @Test
    void abreSessaoComDuracaoInformada() throws Exception {
        when(service.abrir(eq(1L), eq(120L))).thenReturn(sessao());

        mockMvc.perform(post("/api/v1/pautas/1/sessao")
                .contentType("application/json")
                .content("{\"duracaoSegundos\": 120}"))
            .andExpect(status().isCreated());
    }

    @Test
    void duracaoNaoPositivaRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/pautas/1/sessao")
                .contentType("application/json")
                .content("{\"duracaoSegundos\": -5}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void sessaoDuplicadaRetorna409() throws Exception {
        when(service.abrir(eq(1L), isNull())).thenThrow(new RegraDeNegocioException("já existe"));

        mockMvc.perform(post("/api/v1/pautas/1/sessao"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void consultaSessao() throws Exception {
        when(service.buscarPorPauta(1L)).thenReturn(sessao());

        mockMvc.perform(get("/api/v1/pautas/1/sessao"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10));
    }
}
