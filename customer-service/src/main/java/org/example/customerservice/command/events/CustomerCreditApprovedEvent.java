package org.example.customerservice.command.events;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CustomerCreditApprovedEvent {
    private UUID customerId;
}
