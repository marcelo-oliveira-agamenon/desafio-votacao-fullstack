package com.desafio.votacao.service;

import com.desafio.votacao.domain.EscolhaVoto;
import com.desafio.votacao.domain.SessaoVotacao;
import com.desafio.votacao.domain.Voto;
import com.desafio.votacao.exception.AssociadoNaoHabilitadoException;
import com.desafio.votacao.exception.RecursoNaoEncontradoException;
import com.desafio.votacao.exception.RegraDeNegocioException;
import com.desafio.votacao.integration.elegibilidade.AssociadoElegibilidadeClient;
import com.desafio.votacao.integration.elegibilidade.StatusElegibilidade;
import com.desafio.votacao.repository.SessaoVotacaoRepository;
import com.desafio.votacao.repository.VotoRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VotoService {

    private static final Logger log = LoggerFactory.getLogger(VotoService.class);

    private final VotoRepository votoRepository;
    private final SessaoVotacaoRepository sessaoRepository;
    private final AssociadoElegibilidadeClient elegibilidadeClient;

    public VotoService(
        VotoRepository votoRepository,
        SessaoVotacaoRepository sessaoRepository,
        AssociadoElegibilidadeClient elegibilidadeClient
    ) {
        this.votoRepository = votoRepository;
        this.sessaoRepository = sessaoRepository;
        this.elegibilidadeClient = elegibilidadeClient;
    }

    @Transactional
    public Voto registrar(Long pautaId, String associadoId, EscolhaVoto escolha) {
        if (elegibilidadeClient.consultar(associadoId).status() == StatusElegibilidade.UNABLE_TO_VOTE) {
            throw new AssociadoNaoHabilitadoException(associadoId);
        }

        SessaoVotacao sessao = sessaoRepository.findByPautaId(pautaId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("A pauta " + pautaId + " não possui sessão de votação"));

        if (!sessao.estaAberta(Instant.now())) {
            throw new RegraDeNegocioException("A sessão de votação da pauta " + pautaId + " não está aberta");
        }
        if (votoRepository.existsBySessaoIdAndAssociadoId(sessao.getId(), associadoId)) {
            throw new RegraDeNegocioException("O associado " + associadoId + " já votou nesta pauta");
        }

        try {
            Voto voto = votoRepository.saveAndFlush(new Voto(sessao, associadoId, escolha));
            log.info("Voto registrado: sessaoId={}, associadoId={}, escolha={}", sessao.getId(), associadoId, escolha);
            return voto;
        } catch (DataIntegrityViolationException ex) {
            // corrida entre requisições simultâneas do mesmo associado
            throw new RegraDeNegocioException("O associado " + associadoId + " já votou nesta pauta");
        }
    }
}
