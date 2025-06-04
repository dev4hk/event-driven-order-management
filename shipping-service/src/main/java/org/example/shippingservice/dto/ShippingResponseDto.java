package org.example.shippingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.ShippingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingResponseDto {
    private UUID shippingId;
    private UUID orderId;
    private ShippingStatus status;
    private LocalDateTime updatedAt;
}

