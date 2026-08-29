package com.mazur.controle_de_horas.controller;

import com.mazur.controle_de_horas.dto.*;
import com.mazur.controle_de_horas.handler.GlobalExceptionHandler;
import com.mazur.controle_de_horas.service.ProjetoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjetoControllerTest {

    @Mock
    private ProjetoService projetoService;

    @InjectMocks
    private ProjetoController projetoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projetoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveCriarProjeto() throws Exception {
        when(projetoService.criarProjeto(any(ProjetoRequest.class))).thenReturn(new ProjetoResponse(
                10L, "Horizonte", "Sistema novo", 1L, "Gustavo", List.of("Gustavo")));

        mockMvc.perform(post("/projetos/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Horizonte\",\"descricao\":\"Sistema novo\",\"criadorId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.criadorNome").value("Gustavo"));
    }

    @Test
    void deveRetornarMenuDeProjetosDoUsuario() throws Exception {
        when(projetoService.listarProjetosDoUsuario(1L))
                .thenReturn(List.of(new ProjetoMenuResponse(10L, "Horizonte")));

        mockMvc.perform(get("/projetos/menu/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Horizonte"));
    }

    @Test
    void deveBuscarProjetoPorId() throws Exception {
        when(projetoService.findById(10L)).thenReturn(new ProjetoResponse(
                10L, "Horizonte", null, 1L, "Gustavo", List.of("Gustavo", "Ana")));

        mockMvc.perform(get("/projetos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membrosNomes.length()").value(2));
    }

    @Test
    void deveRetornar404QuandoProjetoNaoExiste() throws Exception {
        when(projetoService.findById(77L)).thenThrow(new EntityNotFoundException("não encontrado"));

        mockMvc.perform(get("/projetos/77"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAdicionarMembroGerandoNotificacao() throws Exception {
        when(projetoService.adicionarMembro(eq(10L), any(MembroRequest.class)))
                .thenReturn(new NotificacaoResponse(5L, "Ana", "Horizonte",
                        com.mazur.controle_de_horas.model.StatusNotificacao.PENDENTE, null));

        mockMvc.perform(post("/projetos/10/membros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeUsuario\":\"Ana\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destinatarioNome").value("Ana"));
    }
}
