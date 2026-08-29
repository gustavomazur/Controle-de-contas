package com.mazur.controle_de_horas.controller;

import com.mazur.controle_de_horas.dto.MembroRequest;
import com.mazur.controle_de_horas.dto.NotificacaoResponse;
import com.mazur.controle_de_horas.dto.ProjetoMenuResponse;
import com.mazur.controle_de_horas.dto.ProjetoRequest;
import com.mazur.controle_de_horas.dto.ProjetoResponse;
import com.mazur.controle_de_horas.service.ProjetoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService projetoService;

    //http://localhost:8080/projetos/create
    @PostMapping("/create")
    public ResponseEntity<ProjetoResponse> insert(@Valid @RequestBody ProjetoRequest request) {
        return ResponseEntity.ok(projetoService.criarProjeto(request));
    }

    //http://localhost:8080/projetos/menu/1
    @GetMapping("/menu/{usuarioId}")
    public ResponseEntity<List<ProjetoMenuResponse>> menu(@PathVariable Long usuarioId) {
        return ResponseEntity.status(200).body(projetoService.listarProjetosDoUsuario(usuarioId));
    }

    //http://localhost:8080/projetos/1
    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponse> buscaPorID(@PathVariable long id) {
        ProjetoResponse response = projetoService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    //http://localhost:8080/projetos/1/membros
    @PostMapping("/{id}/membros")
    public ResponseEntity<NotificacaoResponse> adicionarMembro(@PathVariable Long id,
                                                               @Valid @RequestBody MembroRequest request) {
        return ResponseEntity.ok(projetoService.adicionarMembro(id, request));
    }
}
