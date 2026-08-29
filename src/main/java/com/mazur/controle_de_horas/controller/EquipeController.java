package com.mazur.controle_de_horas.controller;

import com.mazur.controle_de_horas.dto.EquipeRequest;
import com.mazur.controle_de_horas.dto.EquipeResponse;
import com.mazur.controle_de_horas.dto.MembroRequest;
import com.mazur.controle_de_horas.service.EquipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipes")
@RequiredArgsConstructor
public class EquipeController {

    private final EquipeService equipeService;

    //http://localhost:8080/equipes/create
    @PostMapping("/create")
    public ResponseEntity<EquipeResponse> insert(@Valid @RequestBody EquipeRequest request) {
        return ResponseEntity.ok(equipeService.criarEquipe(request));
    }

    //http://localhost:8080/equipes/projeto/1
    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<List<EquipeResponse>> listarPorProjeto(@PathVariable Long projetoId) {
        return ResponseEntity.status(200).body(equipeService.listarPorProjeto(projetoId));
    }

    //http://localhost:8080/equipes/1/membros
    @PostMapping("/{id}/membros")
    public ResponseEntity<EquipeResponse> adicionarMembro(@PathVariable Long id,
                                                          @Valid @RequestBody MembroRequest request) {
        return ResponseEntity.ok(equipeService.adicionarMembro(id, request));
    }
}
