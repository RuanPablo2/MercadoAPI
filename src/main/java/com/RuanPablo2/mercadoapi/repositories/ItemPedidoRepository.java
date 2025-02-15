package com.RuanPablo2.mercadoapi.repositories;

import com.RuanPablo2.mercadoapi.entities.ItemPedido;
import com.RuanPablo2.mercadoapi.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    List<ItemPedido> findByPedidoId(Long pedidoId);
}