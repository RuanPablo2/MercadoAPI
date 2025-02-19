package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.OrderItemDTO;
import com.RuanPablo2.mercadoapi.dtos.OrderDTO;
import com.RuanPablo2.mercadoapi.entities.OrderItem;
import com.RuanPablo2.mercadoapi.entities.Order;
import com.RuanPablo2.mercadoapi.entities.Product;
import com.RuanPablo2.mercadoapi.entities.User;
import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import com.RuanPablo2.mercadoapi.exception.BusinessException;
import com.RuanPablo2.mercadoapi.exception.ResourceNotFoundException;
import com.RuanPablo2.mercadoapi.repositories.OrderRepository;
import com.RuanPablo2.mercadoapi.repositories.ProductRepository;
import com.RuanPablo2.mercadoapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderDTO save(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "USR-404"));

        List<OrderItem> orderItem = orderDTO.getItems().stream()
                .map(itemDto -> {
                    Product product = productRepository.findById(itemDto.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found", "PRD-404"));

                    return new OrderItem(null, product, itemDto.getQuantity(), product.getPrice());
                }).collect(Collectors.toList());

        Order order = new Order(user, orderDTO.getStatus(), orderItem);

        order = orderRepository.save(order);

        return new OrderDTO(order);
    }

    @Transactional
    public List<OrderDTO> findAll() {
        return orderRepository.findAll().stream().map(OrderDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO update(Long id, OrderDTO dto) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        order.setStatus(dto.getStatus());

        order = orderRepository.save(order);
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        // Verifica se o pedido já não está cancelado
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new BusinessException("This order has already been canceled.", "ORD-003");
        }

        // Atualiza o status para CANCELED
        order.setStatus(OrderStatus.CANCELED);
        order = orderRepository.save(order);
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO addOrderItem(Long pedidoId, OrderItemDTO orderItemDTO) {
        Order order = orderRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        // Busca o product pelo ID fornecido no orderItemDTO
        Product product = productRepository.findById(orderItemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "PRD-404"));

        // Cria um novo item de order
        OrderItem novoItem = new OrderItem(order, product, orderItemDTO.getQuantity(), product.getPrice());

        // Adicione o novo item à lista de itens do pedido
        order.getItems().add(novoItem);

        order.getTotal();

        order = orderRepository.save(order);

        // Retorne o DTO atualizado do order
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO deleteOrderItem(Long pedidoId, Long itemId) {
        Order order = orderRepository.findById(pedidoId).orElseThrow(() -> new ResourceNotFoundException("Order not found", "ORD-404"));

        // Verifica se o item pertence ao order
        boolean removed = order.getItems().removeIf(item -> item.getId().equals(itemId));

        if (!removed) {
            throw new BusinessException("Order item not found in the specified order", "ORD-005");
        }

        order = orderRepository.save(order);

        return new OrderDTO(order);
    }
}