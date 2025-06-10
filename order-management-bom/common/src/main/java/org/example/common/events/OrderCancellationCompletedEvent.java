package org.example.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.PaymentStatus;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.OrderItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancellationCompletedEvent {
    private UUID orderId;

    private PaymentStatus paymentStatus;

    private ShippingStatus shippingStatus;

    private List<OrderItemDto> items;

    private String message;

    private LocalDateTime cancelledAt;
}
