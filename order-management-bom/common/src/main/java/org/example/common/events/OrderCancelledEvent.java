package org.example.common.events;

import lombok.Data;
import org.example.common.constants.OrderStatus;

import java.util.UUID;

@Data
public class OrderCancelledEvent {
    private UUID orderId;
    private UUID customerId;
    private OrderStatus status;
    private String reason;
}