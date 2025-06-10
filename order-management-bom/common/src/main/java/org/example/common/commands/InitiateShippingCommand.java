package org.example.common.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

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
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String customerName;
    private String customerEmail;
}