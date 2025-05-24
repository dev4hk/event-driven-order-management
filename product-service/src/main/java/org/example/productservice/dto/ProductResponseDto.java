package org.example.productservice.dto;

import lombok.Data;
import org.example.productservice.entity.Product;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductResponseDto {
    private UUID productId;
    private String name;
    private BigDecimal price;
    private String description;
    private int stock;

    public ProductResponseDto(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.stock = product.getStock();
    }
}
