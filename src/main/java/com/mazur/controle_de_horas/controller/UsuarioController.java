package com.mazur.controle_de_horas.controller;

import com.mazur.controle_de_horas.dto.UsuarioRequest;
import com.mazur.controle_de_horas.dto.UsuarioResponse;
import com.mazur.controle_de_horas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    //http://localhost:8080/usuarios/create
    @PostMapping("/create")
    public ResponseEntity<UsuarioResponse> insert(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.criarUsuario(request));
    }

    //http://localhost:8080/usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.status(200).body(usuarioService.listar());
    }

    //http://localhost:8080/usuarios/1
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscaPorID(@PathVariable long id) {
        UsuarioResponse response = usuarioService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    //http://localhost:8080/usuarios/buscar?nome=Gustavo
    @GetMapping("/buscar")
    public ResponseEntity<UsuarioResponse> buscaPorNome(@RequestParam String nome) {
        return ResponseEntity.status(200).body(usuarioService.buscarPorNome(nome));
    }
}
