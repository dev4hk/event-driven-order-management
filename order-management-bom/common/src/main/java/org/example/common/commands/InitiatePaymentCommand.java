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
public class InitiatePaymentCommand {

    @TargetAggregateIdentifier
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal totalAmount;

    private String customerName;
    private String customerEmail;
    private String address;
    private String city;
    private String state;
    private String zipCode;
}
