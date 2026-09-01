package com.desafio.votacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "sessao_votacao")
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false, unique = true)
    private Pauta pauta;

    @Column(nullable = false)
    private Instant abertura;

    @Column(nullable = false)
    private Instant encerramento;

    protected SessaoVotacao() {
    }

    public SessaoVotacao(Pauta pauta, Instant abertura, Duration duracao) {
        this.pauta = pauta;
        this.abertura = abertura;
        this.encerramento = abertura.plus(duracao);
    }

    public boolean estaAberta(Instant momento) {
        return !momento.isBefore(abertura) && momento.isBefore(encerramento);
    }

    public Long getId() {
        return id;
    }

    public Pauta getPauta() {
        return pauta;
    }

    public Instant getAbertura() {
        return abertura;
    }

    public Instant getEncerramento() {
        return encerramento;
    }
}
