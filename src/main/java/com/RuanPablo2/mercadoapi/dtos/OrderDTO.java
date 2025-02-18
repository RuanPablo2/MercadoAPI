package com.RuanPablo2.mercadoapi.dtos;

import com.RuanPablo2.mercadoapi.entities.Order;
import com.RuanPablo2.mercadoapi.entities.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {

    private Long id;

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long userId;

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    private List<OrderItemDTO> items;

    private BigDecimal total;

    @NotNull(message = "O status do pedido é obrigatório")
    private OrderStatus status;

    @NotNull(message = "O endereço de entrega é obrigatório")
    private AddressDTO deliveryAddress;

    public OrderDTO(Order entity) {
        id = entity.getId();
        userId = entity.getUser().getId();
        items = entity.getItems().stream().map(OrderItemDTO::new).toList();
        total = entity.getTotal();
        status = entity.getStatus(); // enum para String

        // Se o usuário tiver um endereço cadastrado, ele é usado como padrão
        if (entity.getUser().getAddress() != null) {
            deliveryAddress = new AddressDTO(entity.getUser().getAddress());
        }
    }
}