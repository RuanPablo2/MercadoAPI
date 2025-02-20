package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.ProductDTO;
import com.RuanPablo2.mercadoapi.entities.Product;
import com.RuanPablo2.mercadoapi.exception.ResourceNotFoundException;
import com.RuanPablo2.mercadoapi.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public Page<ProductDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductDTO::new);
    }

    public ProductDTO findById(Long id){
        Product result = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found", "PRD-404"));
        return new ProductDTO(result);
    }

    @Transactional
    public ProductDTO save(ProductDTO dto) {
        Product product = new Product(dto);
        product = productRepository.save(product);
        return new ProductDTO(product);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "PRD-404"));
        product.updateData(dto);
        return new ProductDTO(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found", "PRD-404");
        }
        productRepository.deleteById(id);
    }
}