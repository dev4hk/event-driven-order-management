package org.example.common.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Builder
public class ValidateCustomerCommand {

    @TargetAggregateIdentifier
    private UUID customerId;

}
