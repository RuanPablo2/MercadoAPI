package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.request.OrderItemRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.request.OrderStatusUpdateRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.response.OrderDTO;
import com.RuanPablo2.mercadoapi.entities.Order;
import com.RuanPablo2.mercadoapi.entities.OrderItem;
import com.RuanPablo2.mercadoapi.entities.Product;
import com.RuanPablo2.mercadoapi.entities.User;
import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import com.RuanPablo2.mercadoapi.entities.enums.Role;
import com.RuanPablo2.mercadoapi.exception.BusinessException;
import com.RuanPablo2.mercadoapi.exception.ResourceNotFoundException;
import com.RuanPablo2.mercadoapi.repositories.OrderRepository;
import com.RuanPablo2.mercadoapi.repositories.ProductRepository;
import com.RuanPablo2.mercadoapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    public Page<OrderDTO> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderDTO::new);
    }

    @Transactional
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatusUpdateRequestDTO requestDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        OrderStatus currentStatus = order.getCurrentStatus();

        if (isFinalStatus(currentStatus)) {
            throw new BusinessException("Order status cannot be updated", "ORD-006");
        }

        validateStatusTransition(currentStatus, requestDTO.getStatus());

        if (requestDTO.getStatus() == OrderStatus.OUT_FOR_DELIVERY && order.getTrackingCode() == null) {
            order.setTrackingCode(generateTrackingCode(order));
        }

        order.addStatusHistory(requestDTO.getStatus());

        return new OrderDTO(order);
    }

    private boolean isFinalStatus(OrderStatus status) {
        return status == OrderStatus.CANCELED || status == OrderStatus.COMPLETED;
    }

    private String generateTrackingCode(Order order) {
        return "TRK-" + order.getId() + "-" + System.currentTimeMillis();
    }

    @Transactional
    public OrderDTO findByTrackingCode(String trackingCode) {
        Order order = orderRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with tracking code", "ORD-404"));
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO cancelOrder(Long orderId, String userEmail, Role userRole) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        OrderStatus currentStatus = order.getCurrentStatus();

        if (currentStatus == OrderStatus.CANCELED || currentStatus == OrderStatus.COMPLETED || currentStatus == OrderStatus.OUT_FOR_DELIVERY) {
            throw new BusinessException("Order cannot be canceled", "ORD-003");
        }

        if (userRole.equals(Role.ROLE_CLIENT)) {
            if (!order.getUser().getEmail().equals(userEmail)) {
                throw new BusinessException("You cannot cancel orders that are not yours", "ORD-004");
            }
            if (!(currentStatus == OrderStatus.CART || currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.PROCESSING)) {
                throw new BusinessException("Clients can only cancel orders in CART, PENDING or PROCESSING status", "ORD-005");
            }
        }

        if (currentStatus == OrderStatus.CART || currentStatus == OrderStatus.PENDING) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.releaseReservedStock(item.getQuantity());
            }
        }

        if (currentStatus == OrderStatus.PAID || currentStatus == OrderStatus.PROCESSING) {
            order.addStatusHistory(OrderStatus.REFUNDED);
            // TO DO Integrate with payment service for refund
        } else {
            order.addStatusHistory(OrderStatus.CANCELED);
        }

        orderRepository.save(order);
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO createCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "USR-404"));

        Order cart = new Order(user);
        orderRepository.save(cart);
        return new OrderDTO(cart);
    }

    @Transactional
    public OrderDTO addItemToCart(Long orderId, OrderItemRequestDTO itemRequest, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("This order does not belong to you", "ORD-008");
        }

        if (order.getCurrentStatus() != OrderStatus.CART) {
            throw new BusinessException("Order is not in CART status", "ORD-009");
        }

        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "PRD-404"));

        int currentQuantity = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .mapToInt(OrderItem::getQuantity)
                .sum();

        int newTotalQuantity = currentQuantity + itemRequest.getQuantity();

        if (newTotalQuantity > product.getStockQuantity()) {
            throw new BusinessException("Insufficient stock available for product: " + product.getName(), "STK-001");
        }

        // Se o item já existe, atualiza a quantidade; senão, cria um novo
        Optional<OrderItem> existingItemOpt = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            OrderItem existingItem = existingItemOpt.get();
            int additionalQuantity = itemRequest.getQuantity();
            // Reserve apenas a quantidade adicional
            product.reserveStock(additionalQuantity);
            existingItem.setQuantity(existingItem.getQuantity() + additionalQuantity);
        } else {
            // Reserva a quantidade desejada
            product.reserveStock(itemRequest.getQuantity());
            OrderItem newItem = new OrderItem();
            newItem.setProduct(product);
            newItem.setQuantity(itemRequest.getQuantity());
            newItem.setUnitPrice(product.getPrice());
            order.addItem(newItem);
        }

        orderRepository.save(order);
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO confirmPayment(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("This order does not belong to you", "ORD-008");
        }

        if (order.getCurrentStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Order is not in PENDING status", "ORD-010");
        }

        order.addStatusHistory(OrderStatus.PAID);

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.finalizeReservation(item.getQuantity());
        }

        orderRepository.save(order);
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO checkout(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("This order does not belong to you", "ORD-008");
        }

        if (order.getCurrentStatus() != OrderStatus.CART) {
            throw new BusinessException("Order is not in CART status", "ORD-009");
        }

        order.addStatusHistory(OrderStatus.PENDING);
        orderRepository.save(order);
        return new OrderDTO(order);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        Map<OrderStatus, List<OrderStatus>> validTransitions = Map.of(
                OrderStatus.CART, List.of(OrderStatus.PENDING, OrderStatus.CANCELED),
                OrderStatus.PENDING, List.of(OrderStatus.PAID, OrderStatus.CANCELED),
                OrderStatus.PAID, List.of(OrderStatus.PROCESSING, OrderStatus.CANCELED),
                OrderStatus.PROCESSING, List.of(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.CANCELED),
                OrderStatus.OUT_FOR_DELIVERY, List.of(OrderStatus.COMPLETED),
                OrderStatus.CANCELED, List.of(),
                OrderStatus.REFUNDED, List.of(),
                OrderStatus.COMPLETED, List.of()
        );

        if (!validTransitions.getOrDefault(current, List.of()).contains(next)) {
            throw new BusinessException("Invalid status transition: " + current + " -> " + next, "ORD-007");
        }
    }
}