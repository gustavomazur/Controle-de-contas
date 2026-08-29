package com.mazur.controle_de_horas.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
        @NotBlank String nome,
        @NotBlank String email,
        @NotBlank String senha) {

}
