package org.example.common.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveProductCommand {
    @TargetAggregateIdentifier
    private UUID productId;
    private UUID orderId;
    private UUID customerId;
    private int quantity;
    private BigDecimal price;
}
