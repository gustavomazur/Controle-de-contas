package com.mazur.Controle_de_contas.dto;

import java.util.Date;

public record CartaoDTO(String nome_do_banco,
                        Date data_do_gasto,
                        int quantas_vezes_foi_parcelado,
                        double valor_parcelado,
                        double valor_da_parcela,
                        double valor_total,
                        double valor_total_do_cartao,
                        String descricao_do_gasto
                        ) {

}
