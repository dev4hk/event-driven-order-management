package org.example.common.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.example.common.constants.PaymentStatus;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RollBackPaymentStatusCommand {

    @TargetAggregateIdentifier
    private UUID paymentId;
    private UUID orderId;
    private PaymentStatus paymentStatus;

}
