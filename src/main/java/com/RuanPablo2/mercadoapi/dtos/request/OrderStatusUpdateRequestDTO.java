package com.RuanPablo2.mercadoapi.dtos.request;

import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusUpdateRequestDTO {

    @NotNull
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}