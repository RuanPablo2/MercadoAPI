package com.RuanPablo2.mercadoapi.controllers;

import com.RuanPablo2.mercadoapi.dtos.ItemPedidoDTO;
import com.RuanPablo2.mercadoapi.dtos.PedidoDTO;
import com.RuanPablo2.mercadoapi.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> findAll() {
        List<PedidoDTO> pedidos = pedidoService.findAll();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> findById(@PathVariable Long id) {
        PedidoDTO pedidoDTO = pedidoService.findById(id);
        return ResponseEntity.ok(pedidoDTO);
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> save(@Valid @RequestBody PedidoDTO dto) {
        PedidoDTO pedidoDTO = pedidoService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> update(@PathVariable Long id, @Valid @RequestBody PedidoDTO dto) {
        PedidoDTO pedidoDTO = pedidoService.update(id, dto);
        return ResponseEntity.ok(pedidoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{pedidoId}/items/{itemId}")
    public ResponseEntity<PedidoDTO> removerItemDoPedido(@PathVariable Long pedidoId, @PathVariable Long itemId) {
        PedidoDTO pedidoDTO = pedidoService.removerItemDoPedido(pedidoId, itemId);
        return ResponseEntity.ok(pedidoDTO);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<PedidoDTO> adicionarItem(@PathVariable Long id,
                                                   @Valid @RequestBody ItemPedidoDTO itemDto) {
        PedidoDTO pedidoDTO = pedidoService.adicionarItemAoPedido(id, itemDto);
        return ResponseEntity.ok(pedidoDTO);
    }
}