package org.example.customerservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Builder
public class ApproveCustomerCreditCommand {

    @TargetAggregateIdentifier
    private UUID customerId;

}
