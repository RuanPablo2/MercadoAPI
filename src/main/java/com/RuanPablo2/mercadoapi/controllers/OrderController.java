package com.RuanPablo2.mercadoapi.controllers;

import com.RuanPablo2.mercadoapi.dtos.OrderItemDTO;
import com.RuanPablo2.mercadoapi.dtos.OrderDTO;
import com.RuanPablo2.mercadoapi.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<OrderDTO> save(@Valid @RequestBody OrderDTO dto) {
        OrderDTO orderDTO = orderService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> update(@PathVariable Long id, @Valid @RequestBody OrderDTO dto) {
        OrderDTO orderDTO = orderService.update(id, dto);
        return ResponseEntity.ok(orderDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDTO> deleteOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        OrderDTO orderDTO = orderService.deleteOrderItem(orderId, itemId);
        return ResponseEntity.ok(orderDTO);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderDTO> addOrderItem(@PathVariable Long id,
                                                 @Valid @RequestBody OrderItemDTO itemDto) {
        OrderDTO orderDTO = orderService.addOrderItem(id, itemDto);
        return ResponseEntity.ok(orderDTO);
    }
}