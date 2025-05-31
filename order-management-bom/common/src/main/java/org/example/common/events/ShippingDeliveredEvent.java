package org.example.common.events;

import lombok.Data;
import org.example.common.constants.ShippingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ShippingDeliveredEvent {
    private UUID shippingId;
    private ShippingStatus newStatus;
    private LocalDateTime updatedAt;
}
