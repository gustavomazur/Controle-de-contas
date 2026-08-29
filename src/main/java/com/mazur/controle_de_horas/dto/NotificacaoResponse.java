package com.mazur.controle_de_horas.dto;

import com.mazur.controle_de_horas.model.StatusNotificacao;

import java.time.LocalDateTime;

public record NotificacaoResponse(
        Long id,
        String destinatarioNome,
        String projetoNome,
        StatusNotificacao status,
        LocalDateTime dataEnvio) {

}
