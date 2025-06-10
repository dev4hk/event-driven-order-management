package org.example.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.example.common.constants.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiatedEvent {
    @TargetAggregateIdentifier
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal totalAmount;
    private PaymentStatus status;
    private LocalDateTime updatedAt;

    private String customerName;
    private String customerEmail;
    private String address;
    private String city;
    private String state;
    private String zipCode;

    private String message;
}
