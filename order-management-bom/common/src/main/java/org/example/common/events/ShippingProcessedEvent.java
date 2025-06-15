package org.example.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.ShippingDetails;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingProcessedEvent {
    private UUID shippingId;
    private UUID orderId;
    private ShippingDetails shippingDetails;
    private ShippingStatus shippingStatus;
    private LocalDateTime updatedAt;
    private String message;
}
