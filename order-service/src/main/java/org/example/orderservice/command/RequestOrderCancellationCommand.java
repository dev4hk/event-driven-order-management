package org.example.orderservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.example.common.dto.OrderItemDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestOrderCancellationCommand {
    @TargetAggregateIdentifier
    private UUID orderId;
    private UUID customerId;
    private UUID paymentId;
    private UUID shippingId;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
    private String reason;
}
