package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.request.OrderItemRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.request.OrderStatusUpdateRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.response.OrderDTO;
import com.RuanPablo2.mercadoapi.dtos.response.OrderSummaryDTO;
import com.RuanPablo2.mercadoapi.entities.Order;
import com.RuanPablo2.mercadoapi.entities.OrderItem;
import com.RuanPablo2.mercadoapi.entities.Product;
import com.RuanPablo2.mercadoapi.entities.User;
import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import com.RuanPablo2.mercadoapi.entities.enums.Role;
import com.RuanPablo2.mercadoapi.exception.*;
import com.RuanPablo2.mercadoapi.repositories.OrderRepository;
import com.RuanPablo2.mercadoapi.repositories.ProductRepository;
import com.RuanPablo2.mercadoapi.repositories.UserRepository;
import com.RuanPablo2.mercadoapi.security.CustomUserDetails;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import org.hibernate.Hibernate;
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

    @Autowired
    PaymentService paymentService;

    @Transactional(readOnly = true)
    public Page<OrderSummaryDTO> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderSummaryDTO::new);
    }

    @Transactional(readOnly = true)
    public OrderDTO findByIdAndValidateOwner(Long orderId, CustomUserDetails userDetails) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));
        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            if (!order.getUser().getId().equals(userDetails.getId())) {
                throw new ForbiddenException("You do not have permission to view this order", "ORD-012");
            }
        }

        Hibernate.initialize(order.getItems());
        Hibernate.initialize(order.getStatusHistory());
        return new OrderDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryDTO> findByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(OrderSummaryDTO::new);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatusUpdateRequestDTO requestDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        OrderStatus currentStatus = order.getCurrentStatus();

        if (isFinalStatus(currentStatus)) {
            throw new OrderStatusException("Order status cannot be updated", "ORD-006");
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

        if (currentStatus == OrderStatus.CART) {
            throw new OrderStatusException("Cart orders cannot be canceled. Please use 'empty cart' instead.", "ORD-015");
        }

        if (currentStatus == OrderStatus.CANCELED || currentStatus == OrderStatus.COMPLETED || currentStatus == OrderStatus.OUT_FOR_DELIVERY) {
            throw new OrderStatusException("Order cannot be canceled", "ORD-003");
        }

        if (userRole.equals(Role.ROLE_CLIENT)) {
            if (!order.getUser().getEmail().equals(userEmail)) {
                throw new ForbiddenException("You cannot cancel orders that are not yours", "ORD-004");
            }
            if (!(currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.PROCESSING)) {
                throw new OrderStatusException("Clients can only cancel orders in PENDING or PROCESSING status", "ORD-005");
            }
        }

        if (currentStatus == OrderStatus.PAID || currentStatus == OrderStatus.PROCESSING) {
            if (order.getPaymentIntentId() == null) {
                throw new PaymentException("Order has no payment intent, refund not possible", "PAY-001");
            }
            try {
                Refund refund = paymentService.refundPayment(order.getPaymentIntentId());
                order.setRefundId(refund.getId());
            } catch (PaymentException e) {
                throw new PaymentException("Failed to refund payment", "PAY-002", e);
            }

            order.addStatusHistory(OrderStatus.REFUNDED);
        } else {
            if (currentStatus == OrderStatus.PENDING) {
                for (OrderItem item : order.getItems()) {
                    item.getProduct().releaseReservedStock(item.getQuantity());
                }
            }
            order.addStatusHistory(OrderStatus.CANCELED);
        }

        orderRepository.save(order);
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO createCart(Long userId) {
        Optional<Order> existingCart = orderRepository.findByUserIdAndCurrentStatus(userId, OrderStatus.CART.toString());
        if (existingCart.isPresent()) {
            return new OrderDTO(existingCart.get());
        }

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
            throw new ForbiddenException("This order does not belong to you", "ORD-008");
        }

        if (order.getCurrentStatus() != OrderStatus.CART) {
            throw new OrderStatusException("Order is not in CART status", "ORD-009");
        }

        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "PRD-404"));

        int currentQuantity = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .mapToInt(OrderItem::getQuantity)
                .sum();

        int newTotalQuantity = currentQuantity + itemRequest.getQuantity();

        if (newTotalQuantity > product.getStockQuantity()) {
            throw new StockException("Insufficient stock available for product: " + product.getName(), "STK-001");
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
    public OrderDTO decreaseItemQuantity(Long orderId, Long productId, int quantity, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("This order does not belong to you", "ORD-008");
        }

        if (order.getCurrentStatus() != OrderStatus.CART) {
            throw new OrderStatusException("Order is not in CART status", "ORD-009");
        }

        OrderItem item = order.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart", "PRD-404"));

        if (item.getQuantity() < quantity) {
            throw new BusinessException("Cannot decrease more than current quantity", "ORD-013");
        }

        item.setQuantity(item.getQuantity() - quantity);
        item.getProduct().releaseReservedStock(quantity);

        if (item.getQuantity() == 0) {
            order.getItems().remove(item);
        }

        orderRepository.save(order);
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO removeItemFromCart(Long orderId, Long productId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("This order does not belong to you", "ORD-008");
        }

        if (order.getCurrentStatus() != OrderStatus.CART) {
            throw new OrderStatusException("Order is not in CART status", "ORD-009");
        }

        OrderItem item = order.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart", "PRD-404"));

        item.getProduct().releaseReservedStock(item.getQuantity());
        order.getItems().remove(item);

        orderRepository.save(order);
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO checkout(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("This order does not belong to you", "ORD-008");
        }

        if (order.getCurrentStatus() != OrderStatus.CART) {
            throw new OrderStatusException("Order is not in CART status", "ORD-009");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessException("Cart is empty. Cannot proceed to checkout.", "ORD-011");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (item.getQuantity() > product.getStockQuantity()) {
                throw new StockException("Insufficient stock available for product: " + product.getName(), "STK-001");
            }
        }

        order.addStatusHistory(OrderStatus.PENDING);
        orderRepository.save(order);
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO emptyCart(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("This order does not belong to you", "ORD-008");
        }

        if (order.getCurrentStatus() != OrderStatus.CART) {
            throw new OrderStatusException("Only carts can be emptied", "ORD-014");
        }

        for (OrderItem item : order.getItems()) {
            item.getProduct().releaseReservedStock(item.getQuantity());
        }

        order.getItems().clear();
        orderRepository.save(order);

        return new OrderDTO(order);
    }

    @Transactional
    public void updateOrderPaymentStatus(String paymentIntentId, OrderStatus status) {
        Order order = orderRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for payment intent", "ORD-404"));
        order.addStatusHistory(status);
        if (status == OrderStatus.PAID) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.finalizeReservation(item.getQuantity());
            }
        } else if (status == OrderStatus.REFUNDED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.increaseStock(item.getQuantity());
            }
        }
        orderRepository.save(order);
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
            throw new OrderStatusException("Invalid status transition: " + current + " -> " + next, "ORD-007");
        }
    }
}