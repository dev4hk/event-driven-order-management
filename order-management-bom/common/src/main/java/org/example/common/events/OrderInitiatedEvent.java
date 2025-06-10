package org.example.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.OrderStatus;
import org.example.common.dto.OrderItemDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInitiatedEvent {
    private UUID orderId;
    private UUID customerId;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
