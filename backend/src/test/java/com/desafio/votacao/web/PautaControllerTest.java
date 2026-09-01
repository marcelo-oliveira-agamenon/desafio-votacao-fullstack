package com.desafio.votacao.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.desafio.votacao.domain.Pauta;
import com.desafio.votacao.service.PautaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PautaController.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PautaService service;

    private static Pauta pautaComId(long id, String titulo) {
        Pauta pauta = new Pauta(titulo, null);
        ReflectionTestUtils.setField(pauta, "id", id);
        return pauta;
    }

    @Test
    void criaPautaRetorna201ComLocation() throws Exception {
        when(service.criar(any(), any())).thenReturn(pautaComId(1L, "Reforma"));

        mockMvc.perform(post("/api/v1/pautas")
                .contentType("application/json")
                .content("""
                    {"titulo": "Reforma", "descricao": "detalhes"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/v1/pautas/1")))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.titulo").value("Reforma"));
    }

    @Test
    void tituloEmBrancoRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/pautas")
                .contentType("application/json")
                .content("""
                    {"titulo": "  "}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("titulo"));
    }

    @Test
    void listaPautas() throws Exception {
        when(service.listar()).thenReturn(List.of(pautaComId(1L, "A"), pautaComId(2L, "B")));

        mockMvc.perform(get("/api/v1/pautas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }
}
