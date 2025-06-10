package org.example.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentDto {

    @NotNull(message = "Customer ID must not be null")
    private UUID customerId;

    @NotNull(message = "Order ID must not be null")
    private UUID paymentId;

    @NotNull(message = "Order ID must not be null")
    private UUID orderId;

    @NotNull(message = "Total amount must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal amount;

}
