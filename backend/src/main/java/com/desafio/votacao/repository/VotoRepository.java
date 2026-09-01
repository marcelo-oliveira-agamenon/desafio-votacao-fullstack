package com.desafio.votacao.repository;

import com.desafio.votacao.domain.EscolhaVoto;
import com.desafio.votacao.domain.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsBySessaoIdAndAssociadoId(Long sessaoId, String associadoId);

    long countBySessaoIdAndEscolha(Long sessaoId, EscolhaVoto escolha);
}
