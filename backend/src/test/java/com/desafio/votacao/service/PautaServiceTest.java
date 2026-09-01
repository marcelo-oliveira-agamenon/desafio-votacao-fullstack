package com.desafio.votacao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.desafio.votacao.domain.Pauta;
import com.desafio.votacao.repository.PautaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository repository;

    @InjectMocks
    private PautaService service;

    @Test
    void criarPersisteERetornaAPauta() {
        when(repository.save(any(Pauta.class))).thenAnswer(inv -> inv.getArgument(0));

        Pauta pauta = service.criar("Reforma do estatuto", "detalhes");

        assertThat(pauta.getTitulo()).isEqualTo("Reforma do estatuto");
        assertThat(pauta.getCriadaEm()).isNotNull();
    }

    @Test
    void listarDelegaAoRepositorio() {
        when(repository.findAll(any(org.springframework.data.domain.Sort.class)))
            .thenReturn(List.of(new Pauta("a", null), new Pauta("b", null)));

        assertThat(service.listar()).hasSize(2);
    }
}
