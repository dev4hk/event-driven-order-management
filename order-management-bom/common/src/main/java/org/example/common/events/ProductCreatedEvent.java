package org.example.common.events;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductCreatedEvent {
    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private boolean active;
}
