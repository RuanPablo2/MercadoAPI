package com.RuanPablo2.mercadoapi.entities;

import com.RuanPablo2.mercadoapi.dtos.request.ProductDTO;
import com.RuanPablo2.mercadoapi.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(name = "tb_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer reservedQuantity = 0;

    @Column(nullable = false)
    private String category;
    private String imageUrl;

    public Product(String name, String description, BigDecimal price, Integer stockQuantity, String category, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.imageUrl = imageUrl;
        this.reservedQuantity = 0;
    }

    public Product(ProductDTO dto) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.price = dto.getPrice();
        this.stockQuantity = dto.getStockQuantity();
        this.reservedQuantity = 0;
    }

    public void updateData(ProductDTO dto) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.price = dto.getPrice();
        this.stockQuantity = dto.getStockQuantity();
    }

    public int getAvailableStock() {
        return this.stockQuantity - this.reservedQuantity;
    }

    public void decreaseStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new BusinessException("Not enough stock available.", "STK-002");
        }
        this.stockQuantity -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void reserveStock(int quantity) {
        if (this.reservedQuantity + quantity > this.stockQuantity) {
            throw new BusinessException("Not enough stock available for reservation.", "STK-003");
        }
        this.reservedQuantity += quantity;
    }

    // Libera uma quantidade reservada
    public void releaseReservedStock(int quantity) {
        if (this.reservedQuantity < quantity) {
            throw new BusinessException("Cannot release more stock than reserved.", "STK-004");
        }
        this.reservedQuantity -= quantity;
    }

    public void finalizeReservation(int quantity) {
        releaseReservedStock(quantity);
        decreaseStock(quantity);
    }
}