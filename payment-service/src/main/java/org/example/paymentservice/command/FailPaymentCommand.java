package org.example.paymentservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.example.common.constants.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailPaymentCommand {

    @TargetAggregateIdentifier
    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String message;
}
