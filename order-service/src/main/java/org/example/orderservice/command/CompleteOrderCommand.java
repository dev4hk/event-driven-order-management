package org.example.orderservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.example.common.constants.OrderStatus;
import org.example.common.constants.PaymentStatus;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.OrderItemDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOrderCommand {
    @TargetAggregateIdentifier
    private UUID orderId;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private ShippingStatus shippingStatus;
}
