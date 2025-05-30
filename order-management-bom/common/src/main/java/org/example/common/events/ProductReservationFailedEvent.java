package org.example.common.events;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductReservationFailedEvent {
    private UUID orderId;
    private UUID productId;
    private UUID customerId;
    private String reason;
}
