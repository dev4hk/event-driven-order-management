package org.example.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.dto.OrderItemDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDto {

    @NotNull(message = "Customer ID must not be null")
    private UUID customerId;

    @NotNull(message = "Order items must not be null")
    @Size(min = 1, message = "At least one order item is required")
    private List<@Valid OrderItemDto> items;

    @NotNull(message = "Total amount must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @NotBlank(message = "Address must not be blank")
    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;

    @NotBlank(message = "City must not be blank")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @NotBlank(message = "State must not be blank")
    @Size(max = 100, message = "State must be at most 100 characters")
    private String state;

    @NotBlank(message = "Zip code must not be blank")
    @Pattern(regexp = "\\d{5}", message = "Zip code must be exactly 5 digits")
    private String zipCode;
}
