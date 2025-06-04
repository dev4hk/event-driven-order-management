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
public class ShippingCreatedEvent {
    private UUID shippingId;
    private UUID orderId;
    private UUID customerId;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String customerName;
    private ShippingStatus status;
    private LocalDateTime updatedAt;
}