package com.mazur.controle_de_horas.controller;

import com.mazur.controle_de_horas.dto.NotificacaoResponse;
import com.mazur.controle_de_horas.model.StatusNotificacao;
import com.mazur.controle_de_horas.service.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificacaoControllerTest {

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private NotificacaoController notificacaoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificacaoController).build();
    }

    private NotificacaoResponse response(StatusNotificacao status) {
        return new NotificacaoResponse(5L, "Ana", "Horizonte", status, null);
    }

    @Test
    void deveListarNotificacoesPendentes() throws Exception {
        when(notificacaoService.listarPendentes(2L)).thenReturn(List.of(response(StatusNotificacao.PENDENTE)));

        mockMvc.perform(get("/notificacoes/pendentes/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDENTE"));
    }

    @Test
    void deveListarTodasAsNotificacoes() throws Exception {
        when(notificacaoService.listarTodas(2L)).thenReturn(List.of(
                response(StatusNotificacao.PENDENTE),
                response(StatusNotificacao.ACEITA)));

        mockMvc.perform(get("/notificacoes/usuario/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deveAceitarNotificacao() throws Exception {
        when(notificacaoService.aceitar(5L)).thenReturn(response(StatusNotificacao.ACEITA));

        mockMvc.perform(post("/notificacoes/5/aceitar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACEITA"));
    }

    @Test
    void deveRecusarNotificacao() throws Exception {
        when(notificacaoService.recusar(5L)).thenReturn(response(StatusNotificacao.RECUSADA));

        mockMvc.perform(post("/notificacoes/5/recusar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECUSADA"));
    }
}
