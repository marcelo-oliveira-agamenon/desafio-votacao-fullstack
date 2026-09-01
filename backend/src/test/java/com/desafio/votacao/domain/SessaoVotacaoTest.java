package com.desafio.votacao.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class SessaoVotacaoTest {

    private final Instant abertura = Instant.parse("2026-01-01T10:00:00Z");
    private final SessaoVotacao sessao = new SessaoVotacao(new Pauta("t", null), abertura, Duration.ofMinutes(1));

    @Test
    void encerramentoEhAberturaMaisDuracao() {
        assertThat(sessao.getEncerramento()).isEqualTo(abertura.plus(Duration.ofMinutes(1)));
    }

    @Test
    void estaAbertaEntreAberturaEEncerramento() {
        assertThat(sessao.estaAberta(abertura)).isTrue();
        assertThat(sessao.estaAberta(abertura.plusSeconds(30))).isTrue();
    }

    @Test
    void estaFechadaAntesDaAberturaENoEncerramento() {
        assertThat(sessao.estaAberta(abertura.minusSeconds(1))).isFalse();
        assertThat(sessao.estaAberta(sessao.getEncerramento())).isFalse();
        assertThat(sessao.estaAberta(sessao.getEncerramento().plusSeconds(1))).isFalse();
    }
}
