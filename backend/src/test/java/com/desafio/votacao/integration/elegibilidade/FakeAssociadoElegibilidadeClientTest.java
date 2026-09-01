package com.desafio.votacao.integration.elegibilidade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.desafio.votacao.exception.CpfInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

class FakeAssociadoElegibilidadeClientTest {

    private static final String CPF_VALIDO = "111.444.777-35";

    private FakeAssociadoElegibilidadeClient client(double probCpfInvalido, double probInelegivel) {
        return new FakeAssociadoElegibilidadeClient(
            new ElegibilidadeProperties(probCpfInvalido, probInelegivel)
        );
    }

    @RepeatedTest(20)
    void semSorteioCpfValidoSempreHabilitado() {
        assertThat(client(0, 0).consultar(CPF_VALIDO).status())
            .isEqualTo(StatusElegibilidade.ABLE_TO_VOTE);
    }

    @Test
    void cpfMalformadoSempre404() {
        assertThatThrownBy(() -> client(0, 0).consultar("123"))
            .isInstanceOf(CpfInvalidoException.class);
    }

    @Test
    void probabilidadeCpfInvalidoMaximaSempre404() {
        assertThatThrownBy(() -> client(1.0, 0).consultar(CPF_VALIDO))
            .isInstanceOf(CpfInvalidoException.class);
    }

    @RepeatedTest(20)
    void probabilidadeInelegivelMaximaSempreUnable() {
        assertThatCode(() -> {
            var status = client(0, 1.0).consultar(CPF_VALIDO).status();
            assertThat(status).isEqualTo(StatusElegibilidade.UNABLE_TO_VOTE);
        }).doesNotThrowAnyException();
    }
}
