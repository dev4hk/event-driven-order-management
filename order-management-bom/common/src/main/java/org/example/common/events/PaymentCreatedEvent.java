package org.example.common.events;

import lombok.Builder;
import lombok.Data;
import org.example.common.constants.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentCreatedEvent {
    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentStatus status;
}

