package org.example.shippingservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.dto.ShippingDetails;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessShippingDto {

    @NotNull(message = "Order ID must not be null")
    private UUID orderId;
    private ShippingDetails shippingDetails;
}
