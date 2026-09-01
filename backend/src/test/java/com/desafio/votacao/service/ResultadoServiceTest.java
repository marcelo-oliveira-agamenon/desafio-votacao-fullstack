package com.desafio.votacao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.desafio.votacao.domain.Desfecho;
import com.desafio.votacao.domain.EscolhaVoto;
import com.desafio.votacao.domain.Pauta;
import com.desafio.votacao.domain.SessaoVotacao;
import com.desafio.votacao.exception.RecursoNaoEncontradoException;
import com.desafio.votacao.repository.SessaoVotacaoRepository;
import com.desafio.votacao.repository.VotoRepository;
import com.desafio.votacao.web.dto.ResultadoResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private ResultadoService service;

    @Test
    void apuraContagensEMarcaSessaoEncerrada() {
        SessaoVotacao fechada =
            new SessaoVotacao(new Pauta("t", null), Instant.now().minusSeconds(120), Duration.ofSeconds(60));
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(fechada));
        when(votoRepository.countBySessaoIdAndEscolha(fechada.getId(), EscolhaVoto.SIM)).thenReturn(3L);
        when(votoRepository.countBySessaoIdAndEscolha(fechada.getId(), EscolhaVoto.NAO)).thenReturn(1L);

        ResultadoResponse r = service.apurar(1L);

        assertThat(r.votosSim()).isEqualTo(3);
        assertThat(r.votosNao()).isEqualTo(1);
        assertThat(r.totalVotos()).isEqualTo(4);
        assertThat(r.sessaoEncerrada()).isTrue();
        assertThat(r.desfecho()).isEqualTo(Desfecho.APROVADA);
    }

    @Test
    void sinalizaSessaoEmAndamento() {
        SessaoVotacao aberta =
            new SessaoVotacao(new Pauta("t", null), Instant.now().minusSeconds(1), Duration.ofMinutes(5));
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(aberta));
        when(votoRepository.countBySessaoIdAndEscolha(aberta.getId(), EscolhaVoto.SIM)).thenReturn(0L);
        when(votoRepository.countBySessaoIdAndEscolha(aberta.getId(), EscolhaVoto.NAO)).thenReturn(0L);

        assertThat(service.apurar(1L).sessaoEncerrada()).isFalse();
    }

    @Test
    void falhaQuandoNaoHaSessao() {
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.apurar(1L)).isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
