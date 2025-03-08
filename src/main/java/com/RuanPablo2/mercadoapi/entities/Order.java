package com.RuanPablo2.mercadoapi.entities;

import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(name = "tb_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(unique = true)
    private String trackingCode;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    private String paymentIntentId;
    private String refundId;

    public Order() {
        this.createdAt = LocalDateTime.now();
    }

    public Order(User user) {
        this();
        this.user = user;
        this.addStatusHistory(OrderStatus.CART);
    }

    public Order(User user, String trackingCode, List<OrderItem> items) {
        this();
        this.user = user;
        this.trackingCode = trackingCode;

        if (items != null) {
            items.forEach(this::addItem);
        }

        this.addStatusHistory(OrderStatus.PENDING);
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
    }

    public void addStatusHistory(OrderStatus status) {
        this.statusHistory.add(new OrderStatusHistory(status, this));
    }

    public OrderStatus getCurrentStatus() {
        return statusHistory.isEmpty()
                ? null
                : statusHistory.get(statusHistory.size() - 1).getStatus();
    }
}