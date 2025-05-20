package com.RuanPablo2.mercadoapi.dtos.response;

import com.RuanPablo2.mercadoapi.entities.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String image;

    private boolean available;
    private boolean lowStock;

    public ProductResponseDTO(Product entity) {
        BeanUtils.copyProperties(entity, this);
        this.available = entity.getStockQuantity() > 0;
        this.lowStock = entity.getStockQuantity() <= 10;
    }
}