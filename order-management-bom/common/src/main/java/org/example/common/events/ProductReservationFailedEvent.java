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
public class ProductReservationFailedEvent {
    private UUID orderId;
    private UUID productId;
    private UUID customerId;
    private String message;
}
