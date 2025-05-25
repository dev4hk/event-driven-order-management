package org.example.common.events;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ShippingDeliveredEvent {
    private UUID shippingId;
    private LocalDateTime deliveredAt;
}
