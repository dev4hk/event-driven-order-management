package org.example.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerValidatedEvent {

    private UUID customerId;
    private UUID orderId;
    private String customerName;
    private String customerEmail;

}
