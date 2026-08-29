package com.mazur.controle_de_horas.service;

import com.mazur.controle_de_horas.dto.AuthResponse;
import com.mazur.controle_de_horas.dto.LoginRequest;
import com.mazur.controle_de_horas.dto.RegisterRequest;
import com.mazur.controle_de_horas.model.Usuario;
import com.mazur.controle_de_horas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email().trim())) {
            throw new IllegalArgumentException("Ja existe uma conta com esse e-mail");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(request.senha()));

        Usuario salvo = usuarioRepository.save(usuario);
        String token = jwtService.gerarToken(salvo.getId(), salvo.getEmail());
        return new AuthResponse(token, salvo.getId(), salvo.getNome(), salvo.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha invalidos"));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new IllegalArgumentException("E-mail ou senha invalidos");
        }

        String token = jwtService.gerarToken(usuario.getId(), usuario.getEmail());
        return new AuthResponse(token, usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
