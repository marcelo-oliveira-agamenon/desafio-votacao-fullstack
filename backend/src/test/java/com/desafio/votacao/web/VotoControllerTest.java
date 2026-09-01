package com.desafio.votacao.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.desafio.votacao.domain.EscolhaVoto;
import com.desafio.votacao.domain.Pauta;
import com.desafio.votacao.domain.SessaoVotacao;
import com.desafio.votacao.domain.Voto;
import com.desafio.votacao.exception.AssociadoNaoHabilitadoException;
import com.desafio.votacao.exception.RegraDeNegocioException;
import com.desafio.votacao.service.VotoService;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VotoController.class)
class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotoService service;

    private static Voto voto() {
        Pauta pauta = new Pauta("t", null);
        ReflectionTestUtils.setField(pauta, "id", 1L);
        SessaoVotacao sessao = new SessaoVotacao(pauta, Instant.now(), Duration.ofMinutes(1));
        Voto voto = new Voto(sessao, "111.444.777-35", EscolhaVoto.SIM);
        ReflectionTestUtils.setField(voto, "id", 5L);
        return voto;
    }

    @Test
    void registraVotoRetorna201() throws Exception {
        when(service.registrar(eq(1L), eq("111.444.777-35"), eq(EscolhaVoto.SIM))).thenReturn(voto());

        mockMvc.perform(post("/api/v1/pautas/1/votos")
                .contentType("application/json")
                .content("""
                    {"associadoId": "111.444.777-35", "escolha": "SIM"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.escolha").value("SIM"));
    }

    @Test
    void escolhaAusenteRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/pautas/1/votos")
                .contentType("application/json")
                .content("""
                    {"associadoId": "111.444.777-35"}
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void associadoInelegivelRetorna404() throws Exception {
        when(service.registrar(any(), any(), any())).thenThrow(new AssociadoNaoHabilitadoException("111.444.777-35"));

        mockMvc.perform(post("/api/v1/pautas/1/votos")
                .contentType("application/json")
                .content("""
                    {"associadoId": "111.444.777-35", "escolha": "NAO"}
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void votoDuplicadoRetorna409() throws Exception {
        when(service.registrar(any(), any(), any())).thenThrow(new RegraDeNegocioException("já votou"));

        mockMvc.perform(post("/api/v1/pautas/1/votos")
                .contentType("application/json")
                .content("""
                    {"associadoId": "111.444.777-35", "escolha": "SIM"}
                    """))
            .andExpect(status().isConflict());
    }
}
