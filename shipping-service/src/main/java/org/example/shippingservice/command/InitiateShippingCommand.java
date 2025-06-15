package org.example.shippingservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.example.common.dto.ShippingDetails;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitiateShippingCommand {
    @TargetAggregateIdentifier
    private UUID shippingId;
    private UUID orderId;
    private UUID customerId;
    private ShippingDetails shippingDetails;
}