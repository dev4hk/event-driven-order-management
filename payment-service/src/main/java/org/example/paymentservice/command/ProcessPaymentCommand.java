package org.example.paymentservice.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ProcessPaymentCommand {
    @TargetAggregateIdentifier
    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
}
