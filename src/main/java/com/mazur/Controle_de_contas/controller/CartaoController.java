package com.mazur.Controle_de_contas.controller;

import com.mazur.Controle_de_contas.dto.CartaoDTO;
import com.mazur.Controle_de_contas.mapper.CartaoMapper;
import com.mazur.Controle_de_contas.model.Cartao;
import com.mazur.Controle_de_contas.service.CartaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/cartao")
@RequiredArgsConstructor
public class CartaoController {
    private final CartaoService cartaoService;

    @PostMapping("/create")
    public ResponseEntity<CartaoDTO> insert(@Valid @RequestBody CartaoDTO dto) {
        Cartao cartao = cartaoService.criarCartao(CartaoMapper.paraEntidade(dto));
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(cartao.getId()).toUri();
        return ResponseEntity.created(uri).body(CartaoMapper.paraDTO(cartao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartaoDTO> getCartaoPorId(@PathVariable long id) {
        Cartao cartao = cartaoService.getCartaoPorId(id);
        return ResponseEntity.status(200).body(CartaoMapper.paraDTO(cartao));
    }


}
