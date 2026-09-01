package com.desafio.votacao.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.desafio.votacao.domain.Desfecho;
import org.junit.jupiter.api.Test;

class ResultadoResponseTest {

    @Test
    void aprovadaQuandoSimVenceEContaOsTotais() {
        ResultadoResponse r = ResultadoResponse.of(1L, true, 7, 3);
        assertThat(r.desfecho()).isEqualTo(Desfecho.APROVADA);
        assertThat(r.totalVotos()).isEqualTo(10);
    }

    @Test
    void rejeitadaQuandoNaoVence() {
        assertThat(ResultadoResponse.of(1L, true, 2, 5).desfecho()).isEqualTo(Desfecho.REJEITADA);
    }

    @Test
    void empateInclusiveSemVotos() {
        assertThat(ResultadoResponse.of(1L, true, 4, 4).desfecho()).isEqualTo(Desfecho.EMPATE);
        assertThat(ResultadoResponse.of(1L, false, 0, 0).desfecho()).isEqualTo(Desfecho.EMPATE);
    }
}
