package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.request.ProductRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.response.ProductResponseDTO;
import com.RuanPablo2.mercadoapi.entities.Product;
import com.RuanPablo2.mercadoapi.exception.BusinessException;
import com.RuanPablo2.mercadoapi.exception.ResourceNotFoundException;
import com.RuanPablo2.mercadoapi.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductResponseDTO::new);
    }

    public ProductResponseDTO findById(Long id){
        Product result = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found", "PRD-404"));
        return new ProductResponseDTO(result);
    }

    @Transactional
    public ProductResponseDTO save(ProductRequestDTO dto) {
        Product product = new Product(dto);
        product = productRepository.save(product);
        return new ProductResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {

        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be positive", "PRD-VAL");
        }

        if (dto.getStockQuantity() == null || dto.getStockQuantity() <= 0) {
            throw new BusinessException("Stock quantity must be non-negative", "PRD-VAL");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "PRD-404"));
        product.updateData(dto);
        return new ProductResponseDTO(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found", "PRD-404");
        }
        productRepository.deleteById(id);
    }
}