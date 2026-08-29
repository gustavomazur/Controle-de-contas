package com.mazur.controle_de_horas.controller;

import com.mazur.controle_de_horas.dto.RegistroHorasRequest;
import com.mazur.controle_de_horas.dto.RegistroHorasResponse;
import com.mazur.controle_de_horas.dto.RelatorioProjetoResponse;
import com.mazur.controle_de_horas.dto.RelatorioUsuarioResponse;
import com.mazur.controle_de_horas.service.RegistroHorasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RegistroHorasControllerTest {

    @Mock
    private RegistroHorasService registroHorasService;

    @InjectMocks
    private RegistroHorasController registroHorasController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(registroHorasController).build();
    }

    private RegistroHorasResponse response() {
        return new RegistroHorasResponse(1L, "Estudando MYSQL", "1:30",
                LocalDate.of(2026, 8, 22), "Horizonte", "Time Alfa", "Gustavo");
    }

    @Test
    void deveRegistrarHoras() throws Exception {
        when(registroHorasService.registrar(any(RegistroHorasRequest.class))).thenReturn(response());

        mockMvc.perform(post("/registros-horas/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Estudando MYSQL\",\"horas\":\"1:30\"," +
                                "\"data\":\"2026-08-22\",\"projetoId\":10,\"equipeId\":20,\"usuarioId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.horas").value("1:30"));
    }

    @Test
    void deveListarRegistrosPorUsuario() throws Exception {
        when(registroHorasService.listarPorUsuario(1L)).thenReturn(List.of(response()));

        mockMvc.perform(get("/registros-horas/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioNome").value("Gustavo"));
    }

    @Test
    void deveListarRegistrosPorProjeto() throws Exception {
        when(registroHorasService.listarPorProjeto(10L)).thenReturn(List.of(response()));

        mockMvc.perform(get("/registros-horas/projeto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projetoNome").value("Horizonte"));
    }

    @Test
    void deveListarRegistrosPorEquipe() throws Exception {
        when(registroHorasService.listarPorEquipe(20L)).thenReturn(List.of(response()));

        mockMvc.perform(get("/registros-horas/equipe/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].equipeNome").value("Time Alfa"));
    }

    @Test
    void deveRetornarRelatorioDoProjeto() throws Exception {
        when(registroHorasService.gerarRelatorio(eq(10L), eq(1L))).thenReturn(new RelatorioProjetoResponse(
                10L,
                "Horizonte",
                "4:00",
                List.of(new RelatorioUsuarioResponse("Gustavo", "3:00", 2L)),
                List.of(response())));

        mockMvc.perform(get("/registros-horas/relatorio/projeto/10").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tempoTotal").value("4:00"))
                .andExpect(jsonPath("$.porUsuario[0].tempoTotal").value("3:00"));
    }
}
