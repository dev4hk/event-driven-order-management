package org.example.customerservice.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Builder
public class CreateCustomerCommand {
    @TargetAggregateIdentifier
    private final UUID customerId;
    private final String name;
    private final String email;
}
