package org.example.productservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductResponseDto {
    private UUID productId;
    private String name;
    private BigDecimal price;
}
