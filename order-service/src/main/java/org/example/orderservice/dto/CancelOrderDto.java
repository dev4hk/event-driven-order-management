package org.example.orderservice.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderDto {

    @NotNull(message = "Order ID must not be null")
    private UUID orderId;

    @NotNull(message = "Customer ID must not be null")
    private UUID customerId;

    @Size(max = 255, message = "Reason must be at most 255 characters")
    private String reason;
}
