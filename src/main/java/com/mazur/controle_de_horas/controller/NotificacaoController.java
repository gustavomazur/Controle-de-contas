package com.mazur.controle_de_horas.controller;

import com.mazur.controle_de_horas.dto.NotificacaoResponse;
import com.mazur.controle_de_horas.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    //http://localhost:8080/notificacoes/pendentes/1
    @GetMapping("/pendentes/{usuarioId}")
    public ResponseEntity<List<NotificacaoResponse>> pendentes(@PathVariable Long usuarioId) {
        return ResponseEntity.status(200).body(notificacaoService.listarPendentes(usuarioId));
    }

    //http://localhost:8080/notificacoes/usuario/1
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacaoResponse>> todas(@PathVariable Long usuarioId) {
        return ResponseEntity.status(200).body(notificacaoService.listarTodas(usuarioId));
    }

    //http://localhost:8080/notificacoes/1/aceitar
    @PostMapping("/{id}/aceitar")
    public ResponseEntity<NotificacaoResponse> aceitar(@PathVariable Long id) {
        return ResponseEntity.ok(notificacaoService.aceitar(id));
    }

    //http://localhost:8080/notificacoes/1/recusar
    @PostMapping("/{id}/recusar")
    public ResponseEntity<NotificacaoResponse> recusar(@PathVariable Long id) {
        return ResponseEntity.ok(notificacaoService.recusar(id));
    }
}
