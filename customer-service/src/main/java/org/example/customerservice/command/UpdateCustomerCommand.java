package org.example.customerservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerCommand {
    @TargetAggregateIdentifier
    private UUID customerId;
    private String name;
    private String email;
    private boolean active;
    private boolean creditApproved;
}
