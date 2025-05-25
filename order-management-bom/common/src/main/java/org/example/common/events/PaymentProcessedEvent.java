package org.example.common.events;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentProcessedEvent {
    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
}
