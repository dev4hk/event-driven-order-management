package org.example.customerservice.command.events;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CustomerCreditApprovedEvent {
    private UUID customerId;
}
