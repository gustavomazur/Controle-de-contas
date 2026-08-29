package com.mazur.controle_de_horas.controller;

import com.mazur.controle_de_horas.dto.EquipeRequest;
import com.mazur.controle_de_horas.dto.EquipeResponse;
import com.mazur.controle_de_horas.dto.MembroRequest;
import com.mazur.controle_de_horas.service.EquipeService;
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
class EquipeControllerTest {

    @Mock
    private EquipeService equipeService;

    @InjectMocks
    private EquipeController equipeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(equipeController).build();
    }

    @Test
    void deveCriarEquipe() throws Exception {
        when(equipeService.criarEquipe(any(EquipeRequest.class))).thenReturn(new EquipeResponse(
                20L, "Time Alfa", 10L, "Horizonte", List.of()));

        mockMvc.perform(post("/equipes/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Time Alfa\",\"projetoId\":10,\"criadorId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.nome").value("Time Alfa"));
    }

    @Test
    void deveListarEquipesDoProjeto() throws Exception {
        when(equipeService.listarPorProjeto(10L)).thenReturn(List.of(new EquipeResponse(
                20L, "Time Alfa", 10L, "Horizonte", List.of("Gustavo"))));

        mockMvc.perform(get("/equipes/projeto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].membrosNomes[0]").value("Gustavo"));
    }

    @Test
    void deveAdicionarMembroNaEquipe() throws Exception {
        when(equipeService.adicionarMembro(eq(20L), any(MembroRequest.class))).thenReturn(new EquipeResponse(
                20L, "Time Alfa", 10L, "Horizonte", List.of("Ana")));

        mockMvc.perform(post("/equipes/20/membros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeUsuario\":\"Ana\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membrosNomes[0]").value("Ana"));
    }
}
