package com.mazur.Controle_de_contas.service;

import com.mazur.Controle_de_contas.dto.CartaoDTO;
import com.mazur.Controle_de_contas.mapper.CartaoMapper;
import com.mazur.Controle_de_contas.model.Cartao;
import com.mazur.Controle_de_contas.repository.CartaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartaoService {

    private final CartaoRepository cartaoRepository;

    @Transactional
    public Cartao criarCartao(Cartao cartao) {
        return cartaoRepository.save(cartao);
    }

    public Cartao getCartaoPorId(long id) {
        return cartaoRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Cartao não foi encontrado", id ));
    }
}


