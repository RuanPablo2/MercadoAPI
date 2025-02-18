package com.RuanPablo2.mercadoapi.entities;

import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    public Order() { //Seta a data de criacao automaticamente
        this.createdAt = LocalDateTime.now();
    }

    public Order(User user, OrderStatus status, List<OrderItem> items) {
        this.user = user;
        this.status = status;
        this.items = items;
    }

    public BigDecimal getTotal() { // soma todos os produtos do pedido para dar o total
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}