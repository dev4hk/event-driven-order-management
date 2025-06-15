package org.example.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusUpdatedEvent {
    private UUID orderId;
    private UUID paymentId;
    private PaymentStatus paymentStatus;
    private String message;
    private LocalDateTime updatedAt;
}
