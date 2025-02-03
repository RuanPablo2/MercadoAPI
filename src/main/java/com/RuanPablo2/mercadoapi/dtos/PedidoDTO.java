package com.RuanPablo2.mercadoapi.dtos;

import com.RuanPablo2.mercadoapi.entities.Pedido;
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
public class PedidoDTO {

    private Long id;

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    private List<ItemPedidoDTO> itens;

    private BigDecimal total;

    @NotNull(message = "O status do pedido é obrigatório")
    private String status;

    @NotNull(message = "O endereço de entrega é obrigatório")
    private EnderecoDTO enderecoEntrega;

    public PedidoDTO(Pedido entity) {
        id = entity.getId();
        usuarioId = entity.getUsuario().getId();
        itens = entity.getItens().stream().map(ItemPedidoDTO::new).toList();
        total = entity.getTotal();
        status = entity.getStatus().name(); // enum para String

        // Se o usuário tiver um endereço cadastrado, ele é usado como padrão
        if (entity.getUsuario().getEndereco() != null) {
            enderecoEntrega = new EnderecoDTO(entity.getUsuario().getEndereco());
        }
    }
}