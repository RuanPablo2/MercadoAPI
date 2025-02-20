package com.RuanPablo2.mercadoapi.dtos.response;

import com.RuanPablo2.mercadoapi.entities.OrderStatusHistory;
import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderStatusHistoryDTO {

    private OrderStatus status;
    private LocalDateTime changedAt;

    public OrderStatusHistoryDTO() {}

    public OrderStatusHistoryDTO(OrderStatusHistory entity) {
        this.status = entity.getStatus();
        this.changedAt = entity.getUpdatedAt();
    }
}