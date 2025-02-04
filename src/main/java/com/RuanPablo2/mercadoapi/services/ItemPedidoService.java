package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.repositories.ItemPedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemPedidoService {

    @Autowired
    ItemPedidoRepository itemPedidoRepository;

    @Transactional
    public void delete(Long id) {
        itemPedidoRepository.deleteById(id);
    }
}