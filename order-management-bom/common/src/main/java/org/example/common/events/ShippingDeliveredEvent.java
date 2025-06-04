package org.example.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.ShippingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDeliveredEvent {
    private UUID shippingId;
    private ShippingStatus newStatus;
    private LocalDateTime updatedAt;
}
