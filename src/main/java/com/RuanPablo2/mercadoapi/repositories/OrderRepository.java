package com.RuanPablo2.mercadoapi.repositories;

import com.RuanPablo2.mercadoapi.entities.Order;
import com.RuanPablo2.mercadoapi.entities.User;
import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    Optional<Order> findByTrackingCode(String trackingCode);

    @Query(value = "SELECT o.* FROM tb_order o " +
            "WHERE o.user_id = :userId " +
            "  AND o.id IN ( " +
            "      SELECT osh.order_id FROM tb_order_status_history osh " +
            "      WHERE osh.updated_at = ( " +
            "          SELECT MAX(osh2.updated_at) FROM tb_order_status_history osh2 " +
            "          WHERE osh2.order_id = osh.order_id " +
            "      ) " +
            "      AND osh.status = :status " +
            ")", nativeQuery = true)
    Optional<Order> findByUserIdAndCurrentStatus(@Param("userId") Long userId, @Param("status") String status);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Optional<Order> findByPaymentIntentId(String paymentIntentId);

    @Modifying
    @Query("UPDATE Order o SET o.paymentIntentId = :paymentIntentId WHERE o.id = :orderId")
    void updatePaymentIntentId(@Param("orderId") Long orderId, @Param("paymentIntentId") String paymentIntentId);

    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);
}