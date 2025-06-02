package org.example.common.events;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CustomerValidatedEvent {

    private UUID customerId;
    private UUID orderId;
    private String customerName;
    private String customerEmail;

}
