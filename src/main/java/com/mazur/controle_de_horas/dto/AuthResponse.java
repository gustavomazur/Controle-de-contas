package com.mazur.controle_de_horas.dto;

public record AuthResponse(
        String token,
        Long id,
        String nome,
        String email) {
}
