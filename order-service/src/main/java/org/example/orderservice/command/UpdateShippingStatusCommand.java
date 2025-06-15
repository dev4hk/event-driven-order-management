package org.example.orderservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.example.common.constants.ShippingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShippingStatusCommand {

    @TargetAggregateIdentifier
    private UUID orderId;
    private UUID shippingId;
    private ShippingStatus shippingStatus;
    private String message;
    private LocalDateTime updatedAt;

}
