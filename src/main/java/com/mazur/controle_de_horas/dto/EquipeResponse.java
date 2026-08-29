package com.mazur.controle_de_horas.dto;

import java.util.List;

public record EquipeResponse(
        Long id,
        String nome,
        Long projetoId,
        String projetoNome,
        List<String> membrosNomes) {

}
