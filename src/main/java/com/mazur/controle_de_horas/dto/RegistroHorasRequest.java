package com.mazur.controle_de_horas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;


public record RegistroHorasRequest(
        String descricao,
        @NotBlank String horas,
        @NotNull LocalDate data,
        @NotNull Long projetoId,
        Long equipeId,
        @NotNull Long usuarioId) {

}
