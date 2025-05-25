package org.example.paymentservice.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Builder
public class CancelPaymentCommand {
    @TargetAggregateIdentifier
    private UUID paymentId;
}
