package com.RuanPablo2.mercadoapi.controllers;

import com.RuanPablo2.mercadoapi.dtos.response.OrderDTO;
import com.RuanPablo2.mercadoapi.dtos.request.OrderStatusUpdateRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.request.OrderItemRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.response.OrderSummaryDTO;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<OrderSummaryDTO>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderSummaryDTO> ordersPage = orderService.findAll(pageable);
        return ResponseEntity.ok(ordersPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        OrderDTO orderDTO = orderService.findByIdAndValidateOwner(id, userDetails);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<Page<OrderSummaryDTO>> findMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderSummaryDTO> ordersPage = orderService.findByUserId(userDetails.getId(), pageable);
        return ResponseEntity.ok(ordersPage);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<OrderDTO> createCart(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        OrderDTO cart = orderService.createCart(userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @PostMapping("/{orderId}/items")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<OrderDTO> addItemToCart(@PathVariable Long orderId,
                                                  @RequestBody @Valid OrderItemRequestDTO itemRequest,
                                                  Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        OrderDTO orderDTO = orderService.addItemToCart(orderId, itemRequest, userDetails.getId());
        return ResponseEntity.ok(orderDTO);
    }

    @PatchMapping("/{orderId}/items/{itemId}/decrease")
    public ResponseEntity<OrderDTO> decreaseItemQuantity(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestParam int quantity,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        OrderDTO updatedOrder = orderService.decreaseItemQuantity(orderId, itemId, quantity, userDetails.getId());
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDTO> removeItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        OrderDTO updatedOrder = orderService.removeItemFromCart(orderId, itemId, userDetails.getId());
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{orderId}/checkout")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<OrderDTO> checkout(@PathVariable Long orderId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        OrderDTO orderDTO = orderService.checkout(orderId, userDetails.getId());
        return ResponseEntity.ok(orderDTO);
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestBody @Valid OrderStatusUpdateRequestDTO request) {
        OrderDTO orderDTO = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/{orderId}/empty-cart")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<OrderDTO> emptyCart(@PathVariable Long orderId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        OrderDTO orderDTO = orderService.emptyCart(orderId, userDetails.getId());
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