package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.ItemPedidoDTO;
import com.RuanPablo2.mercadoapi.dtos.PedidoDTO;
import com.RuanPablo2.mercadoapi.entities.ItemPedido;
import com.RuanPablo2.mercadoapi.entities.Pedido;
import com.RuanPablo2.mercadoapi.entities.Produto;
import com.RuanPablo2.mercadoapi.entities.Usuario;
import com.RuanPablo2.mercadoapi.entities.enums.StatusPedido;
import com.RuanPablo2.mercadoapi.repositories.PedidoRepository;
import com.RuanPablo2.mercadoapi.repositories.ProdutoRepository;
import com.RuanPablo2.mercadoapi.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public PedidoDTO save(PedidoDTO pedidoDTO) {
        Usuario usuario = usuarioRepository.findById(pedidoDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<ItemPedido> itensPedido = pedidoDTO.getItens().stream()
                .map(itemDto -> {
                    Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

                    return new ItemPedido(null, produto, itemDto.getQuantidade(), produto.getPreco());
                }).collect(Collectors.toList());

        Pedido pedido = new Pedido(usuario, pedidoDTO.getStatus(), itensPedido);

        pedido = pedidoRepository.save(pedido);

        return new PedidoDTO(pedido);
    }

    @Transactional
    public List<PedidoDTO> findAll() {
        return pedidoRepository.findAll().stream().map(PedidoDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public PedidoDTO findById(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        return new PedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO update(Long id, PedidoDTO dto) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        pedido.setStatus(dto.getStatus());

        pedido = pedidoRepository.save(pedido);
        return new PedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO delete(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Verifica se o pedido já não está cancelado
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RuntimeException("Este pedido já foi cancelado.");
        }

        // Atualiza o status para CANCELADO
        pedido.setStatus(StatusPedido.CANCELADO);
        pedido = pedidoRepository.save(pedido);
        return new PedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO adicionarItemAoPedido(Long pedidoId, ItemPedidoDTO itemPedidoDTO) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Busca o produto pelo ID fornecido no itemPedidoDTO
        Produto produto = produtoRepository.findById(itemPedidoDTO.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Cria um novo item de pedido
        ItemPedido novoItem = new ItemPedido(pedido, produto, itemPedidoDTO.getQuantidade(), produto.getPreco());

        // Adicione o novo item à lista de itens do pedido
        pedido.getItens().add(novoItem);

        pedido.getTotal();

        pedido = pedidoRepository.save(pedido);

        // Retorne o DTO atualizado do pedido
        return new PedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO removerItemDoPedido(Long pedidoId, Long itemId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Verifica se o item pertence ao pedido
        boolean removed = pedido.getItens().removeIf(item -> item.getId().equals(itemId));

        if (!removed) {
            throw new RuntimeException("Item do pedido não encontrado no pedido especificado");
        }

        pedido = pedidoRepository.save(pedido);

        return new PedidoDTO(pedido);
    }
}