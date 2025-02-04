package com.RuanPablo2.mercadoapi.entities;

import com.RuanPablo2.mercadoapi.dtos.ProdutoDTO;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(name = "tb_produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer quantidadeEstoque;
    private String categoria;
    private String imagem;

    public Produto(String nome, String descricao, BigDecimal preco, Integer quantidadeEstoque, String categoria, String imagem) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.categoria = categoria;
        this.imagem = imagem;
    }

    public Produto(ProdutoDTO dto) {
        this.nome = dto.getNome();
        this.descricao = dto.getDescricao();
        this.preco = dto.getPreco();
        this.quantidadeEstoque = dto.getQuantidadeEstoque();
    }

    public void atualizarDados(ProdutoDTO dto) {
        this.nome = dto.getNome();
        this.descricao = dto.getDescricao();
        this.preco = dto.getPreco();
        this.quantidadeEstoque = dto.getQuantidadeEstoque();
    }
}