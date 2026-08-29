package com.mazur.controle_de_horas.dto;

import jakarta.validation.constraints.NotBlank;

public record MembroRequest(
        @NotBlank String nomeUsuario) {

}
