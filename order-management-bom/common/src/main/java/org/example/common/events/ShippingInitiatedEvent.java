package org.example.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.ShippingDetails;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingInitiatedEvent {
    private UUID shippingId;
    private UUID orderId;
    private ShippingDetails shippingDetails;
}