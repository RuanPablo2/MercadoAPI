package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderItemService {

    @Autowired
    OrderItemRepository orderItemRepository;

    @Transactional
    public void delete(Long id) {
        orderItemRepository.deleteById(id);
    }
}