package org.example.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessedEvent {
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private String message;
    private LocalDateTime updatedAt;
}

