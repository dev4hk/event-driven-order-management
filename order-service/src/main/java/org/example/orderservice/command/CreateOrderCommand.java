package org.example.orderservice.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.example.common.dto.OrderItemDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    private final UUID orderId;
    private final UUID customerId;
    private final List<OrderItemDto> items;
    private final BigDecimal totalAmount;
    private final String address;
    private final String city;
    private final String state;
    private final String zipCode;
}
