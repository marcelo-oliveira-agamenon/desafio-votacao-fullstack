package com.desafio.votacao.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CpfValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"111.444.777-35", "11144477735", "529.982.247-25"})
    void aceitaCpfValido(String cpf) {
        assertThat(CpfValidator.isValido(cpf)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"111.444.777-00", "12345678900", "111", "000.000.000-00", "11111111111"})
    void rejeitaCpfInvalido(String cpf) {
        assertThat(CpfValidator.isValido(cpf)).isFalse();
    }

    @Test
    void rejeitaNuloOuVazio() {
        assertThat(CpfValidator.isValido(null)).isFalse();
        assertThat(CpfValidator.isValido("")).isFalse();
    }

    @Test
    void normalizaMantendoApenasDigitos() {
        assertThat(CpfValidator.normalizar("529.982.247-25")).isEqualTo("52998224725");
    }
}
