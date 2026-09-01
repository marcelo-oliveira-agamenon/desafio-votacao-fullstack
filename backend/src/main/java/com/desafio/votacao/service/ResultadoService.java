package com.desafio.votacao.service;

import com.desafio.votacao.domain.EscolhaVoto;
import com.desafio.votacao.domain.SessaoVotacao;
import com.desafio.votacao.exception.RecursoNaoEncontradoException;
import com.desafio.votacao.repository.SessaoVotacaoRepository;
import com.desafio.votacao.repository.VotoRepository;
import com.desafio.votacao.web.dto.ResultadoResponse;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResultadoService {

    private final SessaoVotacaoRepository sessaoRepository;
    private final VotoRepository votoRepository;

    public ResultadoService(SessaoVotacaoRepository sessaoRepository, VotoRepository votoRepository) {
        this.sessaoRepository = sessaoRepository;
        this.votoRepository = votoRepository;
    }

    @Transactional(readOnly = true)
    public ResultadoResponse apurar(Long pautaId) {
        SessaoVotacao sessao = sessaoRepository.findByPautaId(pautaId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("A pauta " + pautaId + " não possui sessão de votação"));

        long votosSim = votoRepository.countBySessaoIdAndEscolha(sessao.getId(), EscolhaVoto.SIM);
        long votosNao = votoRepository.countBySessaoIdAndEscolha(sessao.getId(), EscolhaVoto.NAO);
        boolean encerrada = !sessao.estaAberta(Instant.now());

        return ResultadoResponse.of(pautaId, encerrada, votosSim, votosNao);
    }
}
