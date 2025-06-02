package org.example.common.events;

import lombok.Data;
import org.example.common.constants.ShippingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
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