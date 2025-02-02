package com.RuanPablo2.mercadoapi.entities;

import com.RuanPablo2.mercadoapi.entities.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(name = "tb_pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens;

    public Pedido() { //Seta a data de criacao automaticamente
        this.dataCriacao = LocalDateTime.now();
    }

    public Pedido(Cliente cliente, LocalDateTime dataCriacao, StatusPedido status, List<ItemPedido> itens) {
        this.cliente = cliente;
        this.dataCriacao = dataCriacao;
        this.status = status;
        this.itens = itens;
    }

    public BigDecimal getTotal() { // soma todos os produtos do pedido para dar o total
        return itens.stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}