package com.mazur.controle_de_horas.controller;

import com.mazur.controle_de_horas.dto.UsuarioRequest;
import com.mazur.controle_de_horas.dto.UsuarioResponse;
import com.mazur.controle_de_horas.handler.GlobalExceptionHandler;
import com.mazur.controle_de_horas.service.UsuarioService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveCriarUsuario() throws Exception {
        when(usuarioService.criarUsuario(any(UsuarioRequest.class)))
                .thenReturn(new UsuarioResponse(1L, "Gustavo", "gustavo@teste.com"));

        mockMvc.perform(post("/usuarios/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Gustavo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Gustavo"));
    }

    @Test
    void deveRetornar400QuandoNomeVazio() throws Exception {
        mockMvc.perform(post("/usuarios/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(new UsuarioResponse(1L, "Gustavo", "gustavo@teste.com"));

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Gustavo"));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        when(usuarioService.findById(99L)).thenThrow(new EntityNotFoundException("não encontrado"));

        mockMvc.perform(get("/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarUsuarios() throws Exception {
        when(usuarioService.listar()).thenReturn(List.of(
                new UsuarioResponse(1L, "Gustavo", "gustavo@teste.com"),
                new UsuarioResponse(2L, "Ana", "ana@teste.com")));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deveBuscarPorNome() throws Exception {
        when(usuarioService.buscarPorNome("Ana")).thenReturn(new UsuarioResponse(2L, "Ana", "ana@teste.com"));

        mockMvc.perform(get("/usuarios/buscar").param("nome", "Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }
}
