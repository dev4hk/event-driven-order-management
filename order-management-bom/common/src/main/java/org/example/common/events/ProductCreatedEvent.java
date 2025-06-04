package org.example.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {
    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private boolean active;
}
