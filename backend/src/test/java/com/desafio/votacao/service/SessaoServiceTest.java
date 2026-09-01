package com.desafio.votacao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.desafio.votacao.domain.Pauta;
import com.desafio.votacao.domain.SessaoVotacao;
import com.desafio.votacao.exception.RecursoNaoEncontradoException;
import com.desafio.votacao.exception.RegraDeNegocioException;
import com.desafio.votacao.repository.PautaRepository;
import com.desafio.votacao.repository.SessaoVotacaoRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private SessaoService service;

    @Test
    void abrirUsaDuracaoPadraoDeUmMinutoQuandoNaoInformada() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(new Pauta("t", null)));
        when(sessaoRepository.existsByPautaId(1L)).thenReturn(false);
        when(sessaoRepository.save(any(SessaoVotacao.class))).thenAnswer(inv -> inv.getArgument(0));

        SessaoVotacao sessao = service.abrir(1L, null);

        assertThat(Duration.between(sessao.getAbertura(), sessao.getEncerramento())).isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    void abrirUsaDuracaoInformadaEmSegundos() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(new Pauta("t", null)));
        when(sessaoRepository.existsByPautaId(1L)).thenReturn(false);
        when(sessaoRepository.save(any(SessaoVotacao.class))).thenAnswer(inv -> inv.getArgument(0));

        SessaoVotacao sessao = service.abrir(1L, 120L);

        assertThat(Duration.between(sessao.getAbertura(), sessao.getEncerramento())).isEqualTo(Duration.ofSeconds(120));
    }

    @Test
    void abrirFalhaQuandoPautaNaoExiste() {
        when(pautaRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.abrir(9L, null)).isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void abrirFalhaQuandoJaExisteSessao() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(new Pauta("t", null)));
        when(sessaoRepository.existsByPautaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.abrir(1L, null)).isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void buscarPorPautaRetornaSessaoExistente() {
        SessaoVotacao sessao = new SessaoVotacao(new Pauta("t", null), Instant.now(), Duration.ofMinutes(1));
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessao));

        assertThat(service.buscarPorPauta(1L)).isSameAs(sessao);
    }

    @Test
    void buscarPorPautaFalhaQuandoNaoHaSessao() {
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorPauta(1L)).isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
