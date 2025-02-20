package com.RuanPablo2.mercadoapi.entities;

import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity(name = "tb_order_status_history")
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderStatusHistory() {}

    public OrderStatusHistory(OrderStatus status, Order order) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        this.order = order;
    }
}