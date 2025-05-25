package org.example.orderservice.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Builder
public class CompleteOrderCommand {
    @TargetAggregateIdentifier
    private final UUID orderId;
}
