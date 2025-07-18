package org.example.orderservice.command;

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
public class UpdateCustomerInfoCommand {

    @TargetAggregateIdentifier
    private UUID orderId;
    private UUID customerId;
    private String customerName;
    private String customerEmail;

}
