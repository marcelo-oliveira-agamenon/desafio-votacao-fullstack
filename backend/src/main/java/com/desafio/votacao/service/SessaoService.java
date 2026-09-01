package com.desafio.votacao.service;

import com.desafio.votacao.domain.Pauta;
import com.desafio.votacao.domain.SessaoVotacao;
import com.desafio.votacao.exception.RecursoNaoEncontradoException;
import com.desafio.votacao.exception.RegraDeNegocioException;
import com.desafio.votacao.repository.PautaRepository;
import com.desafio.votacao.repository.SessaoVotacaoRepository;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessaoService {

    private static final Logger log = LoggerFactory.getLogger(SessaoService.class);
    private static final Duration DURACAO_PADRAO = Duration.ofMinutes(1);

    private final SessaoVotacaoRepository sessaoRepository;
    private final PautaRepository pautaRepository;

    public SessaoService(SessaoVotacaoRepository sessaoRepository, PautaRepository pautaRepository) {
        this.sessaoRepository = sessaoRepository;
        this.pautaRepository = pautaRepository;
    }

    @Transactional
    public SessaoVotacao abrir(Long pautaId, Long duracaoSegundos) {
        Pauta pauta = pautaRepository.findById(pautaId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Pauta " + pautaId + " não encontrada"));

        if (sessaoRepository.existsByPautaId(pautaId)) {
            throw new RegraDeNegocioException("A pauta " + pautaId + " já possui uma sessão de votação");
        }

        Duration duracao = duracaoSegundos == null ? DURACAO_PADRAO : Duration.ofSeconds(duracaoSegundos);
        SessaoVotacao sessao = sessaoRepository.save(new SessaoVotacao(pauta, Instant.now(), duracao));
        log.info("Sessão aberta: id={}, pautaId={}, encerramento={}", sessao.getId(), pautaId, sessao.getEncerramento());
        return sessao;
    }

    @Transactional(readOnly = true)
    public SessaoVotacao buscarPorPauta(Long pautaId) {
        return sessaoRepository.findByPautaId(pautaId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("A pauta " + pautaId + " não possui sessão de votação"));
    }
}
