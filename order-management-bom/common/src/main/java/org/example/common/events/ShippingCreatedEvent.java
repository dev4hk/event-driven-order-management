package org.example.common.events;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ShippingCreatedEvent {
    private UUID shippingId;
    private UUID orderId;
    private LocalDateTime shippedAt;
}