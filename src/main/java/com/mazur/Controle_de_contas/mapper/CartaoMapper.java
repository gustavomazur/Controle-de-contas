package com.mazur.Controle_de_contas.mapper;

import com.mazur.Controle_de_contas.dto.CartaoDTO;
import com.mazur.Controle_de_contas.model.Cartao;


public class CartaoMapper {
    public static CartaoDTO paraDTO(Cartao cartao) {
        if (cartao == null) {
            return null;
        }
        return new CartaoDTO(
                cartao.getNome_do_banco(),
                cartao.getData_do_gasto(),
                cartao.getQuantas_vezes_foi_parcelado(),
                cartao.getValor_parcelado(),
                cartao.getValor_da_parcela(),
                cartao.getValor_total(),
                cartao.getValor_total_do_cartao(),
                cartao.getDescricao_do_gasto());
    }
    public static Cartao paraEntidade(CartaoDTO cartaoDTO) {
        if (cartaoDTO == null) {
            return null;
        }

        return new Cartao(
                cartaoDTO.nome_do_banco(),
                cartaoDTO.data_do_gasto(),
                cartaoDTO.quantas_vezes_foi_parcelado(),
                cartaoDTO.valor_parcelado(),
                cartaoDTO.valor_da_parcela(),
                cartaoDTO.valor_total(),
                cartaoDTO.valor_total_do_cartao(),
                cartaoDTO.descricao_do_gasto()
        );
    }
}
