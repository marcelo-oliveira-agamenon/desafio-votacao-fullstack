package com.desafio.votacao.repository;

import com.desafio.votacao.domain.SessaoVotacao;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    boolean existsByPautaId(Long pautaId);

    Optional<SessaoVotacao> findByPautaId(Long pautaId);
}
