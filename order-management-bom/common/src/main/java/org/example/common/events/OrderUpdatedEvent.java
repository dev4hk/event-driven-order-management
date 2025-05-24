package org.example.common.events;

import lombok.Data;
import org.example.common.dto.OrderItemDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderUpdatedEvent {
    private UUID orderId;
    private UUID customerId;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
}