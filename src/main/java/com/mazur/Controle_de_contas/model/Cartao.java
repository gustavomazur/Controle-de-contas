package com.mazur.Controle_de_contas.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.Date;

@Entity
@Table(name = "tb_cartao")
@Getter
@Setter
@RequiredArgsConstructor
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
public class Cartao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome_do_banco;
    private Date data_do_gasto;
    private int quantas_vezes_foi_parcelado;
    private double valor_parcelado;
    private double valor_da_parcela;
    private double valor_total;
    private double valor_total_do_cartao;
    private String descricao_do_gasto;

    public Cartao(String s, Date date, int i, double v, double v1, double v2, double v3, String s1) {
    }
}
