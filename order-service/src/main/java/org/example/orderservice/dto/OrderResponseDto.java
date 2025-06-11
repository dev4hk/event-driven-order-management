package org.example.orderservice.dto;

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
public class OrderResponseDto {
    private UUID orderId;
    private UUID customerId;
    private List<OrderItemDto> items;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private UUID paymentId;
    private UUID shippingId;
    private LocalDateTime updatedAt;
}
