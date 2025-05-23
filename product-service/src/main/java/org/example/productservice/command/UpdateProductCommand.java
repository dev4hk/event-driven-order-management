package org.example.productservice.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class UpdateProductCommand {
    @TargetAggregateIdentifier
    private final UUID productId;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final int stock;
}
