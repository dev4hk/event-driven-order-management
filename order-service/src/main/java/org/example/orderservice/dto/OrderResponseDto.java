package org.example.orderservice.dto;

import lombok.Data;
import org.example.common.dto.OrderItemDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponseDto {
    private UUID orderId;
    private UUID customerId;
    private List<OrderItemDto> items;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
