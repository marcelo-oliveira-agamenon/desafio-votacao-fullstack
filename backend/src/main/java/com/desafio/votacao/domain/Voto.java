package com.desafio.votacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
    name = "voto",
    uniqueConstraints = @UniqueConstraint(columnNames = {"sessao_id", "associado_id"})
)
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoVotacao sessao;

    @Column(name = "associado_id", nullable = false)
    private String associadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EscolhaVoto escolha;

    @Column(name = "registrado_em", nullable = false)
    private Instant registradoEm;

    protected Voto() {
    }

    public Voto(SessaoVotacao sessao, String associadoId, EscolhaVoto escolha) {
        this.sessao = sessao;
        this.associadoId = associadoId;
        this.escolha = escolha;
        this.registradoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public SessaoVotacao getSessao() {
        return sessao;
    }

    public String getAssociadoId() {
        return associadoId;
    }

    public EscolhaVoto getEscolha() {
        return escolha;
    }

    public Instant getRegistradoEm() {
        return registradoEm;
    }
}
