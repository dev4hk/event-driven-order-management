package org.example.productservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateProductDto {
    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
}

