package com.RuanPablo2.mercadoapi.dtos;

import com.RuanPablo2.mercadoapi.entities.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Nome do produto é obrigatório")
    private String name;

    private String description;

    @NotNull(message = "Preço é obrigatório")
    @Min(value = 0, message = "O preço deve ser positivo")
    private BigDecimal price;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "A quantidade deve ser positiva")
    private Integer stockQuantity;

    private String category;

    private String image;

    public ProductDTO(Product entity){
        BeanUtils.copyProperties(entity, this);
    }
}