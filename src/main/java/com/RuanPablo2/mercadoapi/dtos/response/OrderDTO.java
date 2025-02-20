package com.RuanPablo2.mercadoapi.dtos.response;

import com.RuanPablo2.mercadoapi.entities.Order;
import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDTO {

    private Long id;
    private String userEmail;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private String trackingCode;
    private BigDecimal total;
    private List<OrderItemResponseDTO> items;
    private List<OrderStatusHistoryDTO> statusHistory;

    public OrderDTO() {}

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.userEmail = order.getUser().getEmail();
        this.createdAt = order.getCreatedAt();
        this.status = order.getCurrentStatus();
        this.trackingCode = order.getTrackingCode();
        this.total = order.getTotal();
        this.items = order.getItems().stream().map(OrderItemResponseDTO::new).toList();
        this.statusHistory = order.getStatusHistory().stream().map(OrderStatusHistoryDTO::new).toList();
    }
}