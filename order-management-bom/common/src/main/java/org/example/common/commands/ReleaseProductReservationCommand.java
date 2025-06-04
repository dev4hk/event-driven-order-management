package org.example.common.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseProductReservationCommand {
    @TargetAggregateIdentifier
    private UUID productId;
    private UUID orderId;
    private UUID customerId;
    private int quantity;
}
