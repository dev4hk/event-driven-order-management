package org.example.paymentservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentCommand {
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal totalAmount;
}
