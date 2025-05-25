package org.example.common.events;

import lombok.Data;
import org.example.common.constants.OrderStatus;

import java.util.UUID;

@Data
public class OrderCompletedEvent {
    private UUID orderId;
    private OrderStatus status;
}
