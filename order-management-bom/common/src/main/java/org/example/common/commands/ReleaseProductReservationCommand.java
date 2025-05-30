package org.example.common.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Builder
public class ReleaseProductReservationCommand {
    @TargetAggregateIdentifier
    private UUID productId;
    private UUID orderId;
    private UUID customerId;
    private int quantity;
}
