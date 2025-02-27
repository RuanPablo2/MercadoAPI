package com.RuanPablo2.mercadoapi.dtos.response;

import com.RuanPablo2.mercadoapi.entities.Order;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderSummaryDTO {
    private Long id;
    private String userEmail;
    private LocalDateTime createdAt;
    private String currentStatus;
    private String trackingCode;
    private BigDecimal total;
    private List<OrderItemResponseDTO> items;

    public OrderSummaryDTO(Order order) {
        this.id = order.getId();
        this.userEmail = order.getUser().getEmail();
        this.createdAt = order.getCreatedAt();
        this.currentStatus = order.getCurrentStatus() != null ? order.getCurrentStatus().name() : null;
        this.trackingCode = order.getTrackingCode();
        this.total = order.getTotal();
        this.items = order.getItems().stream().map(OrderItemResponseDTO::new).toList();
    }
}