package com.RuanPablo2.mercadoapi.controllers;

import com.RuanPablo2.mercadoapi.dtos.response.OrderDTO;
import com.RuanPablo2.mercadoapi.dtos.request.OrderStatusUpdateRequestDTO;
import com.RuanPablo2.mercadoapi.security.CustomUserDetails;
import com.RuanPablo2.mercadoapi.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> ordersPage = orderService.findAll(pageable);
        return ResponseEntity.ok(ordersPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.findById(id);
        return ResponseEntity.ok(orderDTO);
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestBody @Valid OrderStatusUpdateRequestDTO request) {
        OrderDTO orderDTO = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/tracking/{trackingCode}")
    public ResponseEntity<OrderDTO> findByTrackingCode(@PathVariable String trackingCode) {
        OrderDTO dto = orderService.findByTrackingCode(trackingCode);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        OrderDTO orderDTO = orderService.cancelOrder(orderId, userDetails.getEmail(), userDetails.getRole());
        return ResponseEntity.ok(orderDTO);
    }
}