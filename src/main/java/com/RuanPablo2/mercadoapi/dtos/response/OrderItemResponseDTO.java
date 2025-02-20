package com.RuanPablo2.mercadoapi.dtos.response;

import com.RuanPablo2.mercadoapi.entities.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class OrderItemResponseDTO {

    private Long productId;
    private Integer quantity;
    private String productName;
    private BigDecimal unitPrice;
    private BigDecimal total;

    public OrderItemResponseDTO(OrderItem entity) {
        this.productId = entity.getProduct().getId();
        this.quantity = entity.getQuantity();
        this.productName = entity.getProduct().getName();
        this.unitPrice = entity.getUnitPrice();
        this.total = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
}