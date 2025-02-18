package com.RuanPablo2.mercadoapi.dtos;

import com.RuanPablo2.mercadoapi.entities.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDTO {

    private Long productId;
    private Integer quantity;
    private String productName;
    private BigDecimal unitPrice;

    // Calcula o total do item
    private BigDecimal total;

    public OrderItemDTO(OrderItem entity) {
        productId = entity.getProduct().getId();
        quantity = entity.getQuantity();
        this.productName = entity.getProduct().getName();
        unitPrice = entity.getUnitPrice();
        total = entity.getUnitPrice().multiply(new BigDecimal(entity.getQuantity()));
    }
}