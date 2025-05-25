package org.example.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.common.dto.OrderItemDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateOrderDto {
    @NotNull
    private UUID orderId;

    @NotNull
    private UUID customerId;

    @NotNull
    private List<OrderItemDto> items;

    @NotNull
    private BigDecimal totalAmount;
}
