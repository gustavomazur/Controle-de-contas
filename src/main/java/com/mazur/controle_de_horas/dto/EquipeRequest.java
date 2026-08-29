package com.mazur.controle_de_horas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EquipeRequest(
        @NotBlank String nome,
        @NotNull Long projetoId,
        @NotNull Long criadorId) {

}
