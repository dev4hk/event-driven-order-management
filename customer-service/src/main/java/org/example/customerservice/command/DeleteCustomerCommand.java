package org.example.customerservice.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Builder
public class DeleteCustomerCommand {
    @TargetAggregateIdentifier
    private final UUID customerId;
}
