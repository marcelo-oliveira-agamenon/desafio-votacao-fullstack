package com.desafio.votacao;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.time.Duration;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = {
    "votacao.elegibilidade.probabilidade-cpf-invalido=0",
    "votacao.elegibilidade.probabilidade-inelegivel=0"
})
class VotacaoIntegrationTest {

    private static final String CPF_A = "111.444.777-35";
    private static final String CPF_B = "529.982.247-25";
    private static final String CPF_C = "123.456.789-09";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fluxoCompletoDeVotacao() throws Exception {
        long pautaId = criarPauta();

        mockMvc.perform(post("/api/v1/pautas/{id}/sessao", pautaId)
                .contentType("application/json")
                .content("{\"duracaoSegundos\": 2}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.aberta").value(true));

        votar(pautaId, CPF_A, "SIM").andExpect(status().isCreated());
        votar(pautaId, CPF_A, "SIM").andExpect(status().isConflict());
        votar(pautaId, CPF_B, "NAO").andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/pautas/{id}/resultado", pautaId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.votosSim").value(1))
            .andExpect(jsonPath("$.votosNao").value(1))
            .andExpect(jsonPath("$.sessaoEncerrada").value(false))
            .andExpect(jsonPath("$.desfecho").value("EMPATE"));

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
            mockMvc.perform(get("/api/v1/pautas/{id}/resultado", pautaId))
                .andExpect(jsonPath("$.sessaoEncerrada").value(true)));

        votar(pautaId, CPF_C, "SIM").andExpect(status().isConflict());
    }

    @Test
    void abrirSessaoParaPautaInexistenteRetorna404() throws Exception {
        mockMvc.perform(post("/api/v1/pautas/{id}/sessao", 999_999))
            .andExpect(status().isNotFound());
    }

    @Test
    void votoSemSessaoRetorna404() throws Exception {
        long pautaId = criarPauta();

        votar(pautaId, CPF_A, "SIM").andExpect(status().isNotFound());
    }

    private long criarPauta() throws Exception {
        String body = mockMvc.perform(post("/api/v1/pautas")
                .contentType("application/json")
                .content("{\"titulo\": \"Pauta de teste\", \"descricao\": \"integra\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", greaterThan(0)))
            .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private org.springframework.test.web.servlet.ResultActions votar(long pautaId, String cpf, String escolha)
        throws Exception {
        return mockMvc.perform(post("/api/v1/pautas/{id}/votos", pautaId)
            .contentType("application/json")
            .content("{\"associadoId\": \"" + cpf + "\", \"escolha\": \"" + escolha + "\"}"));
    }
}
