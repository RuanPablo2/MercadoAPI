package com.RuanPablo2.mercadoapi.repositories;

import com.RuanPablo2.mercadoapi.entities.Order;
import com.RuanPablo2.mercadoapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    Optional<Order> findByTrackingCode(String trackingCode);
}