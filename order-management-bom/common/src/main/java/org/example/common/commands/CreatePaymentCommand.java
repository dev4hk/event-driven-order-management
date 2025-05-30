package org.example.common.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CreatePaymentCommand {
    @TargetAggregateIdentifier
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal amount;
}

