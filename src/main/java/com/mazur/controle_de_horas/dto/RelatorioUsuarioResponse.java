package com.mazur.controle_de_horas.dto;

public record RelatorioUsuarioResponse(
        String usuarioNome,
        String tempoTotal,
        Long quantidadeRegistros) {

}
