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
public class ProductReservedEvent {

    private UUID orderId;
    private UUID productId;
    private UUID customerId;
    private int quantity;
    private boolean active;
}
