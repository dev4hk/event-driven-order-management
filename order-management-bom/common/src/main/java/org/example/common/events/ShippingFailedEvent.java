package org.example.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.ShippingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFailedEvent {
    private UUID orderId;
    private UUID shippingId;
    private String message;
    private ShippingStatus shippingStatus;
    private LocalDateTime updatedAt;
}