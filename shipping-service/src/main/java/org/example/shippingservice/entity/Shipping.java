package org.example.shippingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.ShippingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipping {

    @Id
    private UUID shippingId;

    private UUID orderId;

    private UUID customerId;

    private String address;

    private String city;

    private String state;

    private String zipCode;

    private String customerName;

    @Enumerated(EnumType.STRING)
    private ShippingStatus status;

    private LocalDateTime updatedAt;

    private String reason;
}

