package org.example.common.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.example.common.constants.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentStatusCommand {

    @TargetAggregateIdentifier
    private UUID orderId;
    private UUID paymentId;
    private PaymentStatus paymentStatus;
    private String message;
    private LocalDateTime updatedAt;
    private String customerName;
    private String customerEmail;

}
