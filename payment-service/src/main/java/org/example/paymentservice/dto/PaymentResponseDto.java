package org.example.paymentservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentResponseDto {
    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private String status;
}