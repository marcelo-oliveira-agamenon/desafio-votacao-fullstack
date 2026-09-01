package com.desafio.votacao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.desafio.votacao.domain.EscolhaVoto;
import com.desafio.votacao.domain.Pauta;
import com.desafio.votacao.domain.SessaoVotacao;
import com.desafio.votacao.domain.Voto;
import com.desafio.votacao.exception.AssociadoNaoHabilitadoException;
import com.desafio.votacao.exception.RecursoNaoEncontradoException;
import com.desafio.votacao.exception.RegraDeNegocioException;
import com.desafio.votacao.integration.elegibilidade.AssociadoElegibilidadeClient;
import com.desafio.votacao.integration.elegibilidade.ElegibilidadeResponse;
import com.desafio.votacao.integration.elegibilidade.StatusElegibilidade;
import com.desafio.votacao.repository.SessaoVotacaoRepository;
import com.desafio.votacao.repository.VotoRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    private static final String ASSOCIADO = "111.444.777-35";

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private AssociadoElegibilidadeClient elegibilidadeClient;

    @InjectMocks
    private VotoService service;

    private SessaoVotacao sessaoAberta() {
        return new SessaoVotacao(new Pauta("t", null), Instant.now().minusSeconds(1), Duration.ofMinutes(5));
    }

    private void habilitado() {
        when(elegibilidadeClient.consultar(anyString()))
            .thenReturn(new ElegibilidadeResponse(StatusElegibilidade.ABLE_TO_VOTE));
    }

    @Test
    void registraVotoQuandoTudoValido() {
        habilitado();
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessaoAberta()));
        when(votoRepository.existsBySessaoIdAndAssociadoId(any(), any())).thenReturn(false);
        when(votoRepository.saveAndFlush(any(Voto.class))).thenAnswer(inv -> inv.getArgument(0));

        Voto voto = service.registrar(1L, ASSOCIADO, EscolhaVoto.SIM);

        assertThat(voto.getEscolha()).isEqualTo(EscolhaVoto.SIM);
        assertThat(voto.getAssociadoId()).isEqualTo(ASSOCIADO);
    }

    @Test
    void rejeitaAssociadoInelegivelSemTocarNaSessao() {
        when(elegibilidadeClient.consultar(anyString()))
            .thenReturn(new ElegibilidadeResponse(StatusElegibilidade.UNABLE_TO_VOTE));

        assertThatThrownBy(() -> service.registrar(1L, ASSOCIADO, EscolhaVoto.SIM))
            .isInstanceOf(AssociadoNaoHabilitadoException.class);
        verifyNoInteractions(sessaoRepository, votoRepository);
    }

    @Test
    void falhaQuandoPautaNaoTemSessao() {
        habilitado();
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrar(1L, ASSOCIADO, EscolhaVoto.SIM))
            .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void falhaQuandoSessaoEstaFechada() {
        habilitado();
        SessaoVotacao fechada =
            new SessaoVotacao(new Pauta("t", null), Instant.now().minusSeconds(120), Duration.ofSeconds(60));
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(fechada));

        assertThatThrownBy(() -> service.registrar(1L, ASSOCIADO, EscolhaVoto.SIM))
            .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void falhaQuandoAssociadoJaVotou() {
        habilitado();
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessaoAberta()));
        when(votoRepository.existsBySessaoIdAndAssociadoId(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(1L, ASSOCIADO, EscolhaVoto.SIM))
            .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void traduzViolacaoDeUnicidadeEmRegraDeNegocio() {
        habilitado();
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessaoAberta()));
        when(votoRepository.existsBySessaoIdAndAssociadoId(any(), any())).thenReturn(false);
        when(votoRepository.saveAndFlush(any(Voto.class))).thenThrow(new DataIntegrityViolationException("dup"));

        assertThatThrownBy(() -> service.registrar(1L, ASSOCIADO, EscolhaVoto.SIM))
            .isInstanceOf(RegraDeNegocioException.class);
    }
}
