package com.RuanPablo2.mercadoapi.dtos;

import com.RuanPablo2.mercadoapi.entities.ItemPedido;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ItemPedidoDTO {

    private Long produtoId;
    private Integer quantidade;
    private String nomeProduto;
    private BigDecimal precoUnitario;

    // Calcula o total do item
    private BigDecimal total;

    public ItemPedidoDTO(ItemPedido entity) {
        produtoId = entity.getProduto().getId();
        quantidade = entity.getQuantidade();
        this.nomeProduto = entity.getProduto().getNome();
        precoUnitario = entity.getPrecoUnitario();
        total = entity.getPrecoUnitario().multiply(new BigDecimal(entity.getQuantidade()));
    }
}