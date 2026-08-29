package com.mazur.controle_de_horas.dto;

import java.util.List;

public record RelatorioProjetoResponse(
        Long projetoId,
        String projetoNome,
        String tempoTotal,
        List<RelatorioUsuarioResponse> porUsuario,
        List<RegistroHorasResponse> registros) {

}
