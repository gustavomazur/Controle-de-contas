package com.mazur.controle_de_horas.dto;

import java.time.LocalDate;

public record RegistroHorasResponse(
        Long id,
        String descricao,
        String horas,
        LocalDate data,
        String projetoNome,
        String equipeNome,
        String usuarioNome) {

}
