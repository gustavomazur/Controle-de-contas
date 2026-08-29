package com.mazur.controle_de_horas.controller;

import com.mazur.controle_de_horas.dto.RegistroHorasRequest;
import com.mazur.controle_de_horas.dto.RegistroHorasResponse;
import com.mazur.controle_de_horas.dto.RelatorioProjetoResponse;
import com.mazur.controle_de_horas.service.RegistroHorasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registros-horas")
@RequiredArgsConstructor
public class RegistroHorasController {

    private final RegistroHorasService registroHorasService;

    //http://localhost:8080/registros-horas/create
    @PostMapping("/create")
    public ResponseEntity<RegistroHorasResponse> insert(@Valid @RequestBody RegistroHorasRequest request) {
        return ResponseEntity.ok(registroHorasService.registrar(request));
    }

    //http://localhost:8080/registros-horas/usuario/1
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<RegistroHorasResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.status(200).body(registroHorasService.listarPorUsuario(usuarioId));
    }

    //http://localhost:8080/registros-horas/projeto/1
    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<List<RegistroHorasResponse>> listarPorProjeto(@PathVariable Long projetoId) {
        return ResponseEntity.status(200).body(registroHorasService.listarPorProjeto(projetoId));
    }

    //http://localhost:8080/registros-horas/equipe/1
    @GetMapping("/equipe/{equipeId}")
    public ResponseEntity<List<RegistroHorasResponse>> listarPorEquipe(@PathVariable Long equipeId) {
        return ResponseEntity.status(200).body(registroHorasService.listarPorEquipe(equipeId));
    }

    //http://localhost:8080/registros-horas/relatorio/projeto/1?usuarioId=2
    @GetMapping("/relatorio/projeto/{projetoId}")
    public ResponseEntity<RelatorioProjetoResponse> relatorio(@PathVariable Long projetoId,
                                                              @RequestParam Long usuarioId) {
        return ResponseEntity.status(200).body(registroHorasService.gerarRelatorio(projetoId, usuarioId));
    }
}
