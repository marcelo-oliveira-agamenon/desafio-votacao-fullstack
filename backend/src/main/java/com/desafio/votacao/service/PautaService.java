package com.desafio.votacao.service;

import com.desafio.votacao.domain.Pauta;
import com.desafio.votacao.repository.PautaRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PautaService {

    private static final Logger log = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepository repository;

    public PautaService(PautaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Pauta criar(String titulo, String descricao) {
        Pauta pauta = repository.save(new Pauta(titulo, descricao));
        log.info("Pauta criada: id={}, titulo='{}'", pauta.getId(), pauta.getTitulo());
        return pauta;
    }

    @Transactional(readOnly = true)
    public List<Pauta> listar() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "criadaEm"));
    }
}
