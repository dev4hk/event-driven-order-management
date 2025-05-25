package org.example.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequestDto {

    @NotNull(message = "Order ID must not be null")
    private UUID orderId;

    @NotBlank(message = "Payment method must not be blank")
    private String paymentMethod;

    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;
}