package com.mazur.controle_de_horas.dto;

import java.util.List;

public record ProjetoResponse(
        Long id,
        String nome,
        String descricao,
        Long criadorId,
        String criadorNome,
        List<String> membrosNomes) {

}
