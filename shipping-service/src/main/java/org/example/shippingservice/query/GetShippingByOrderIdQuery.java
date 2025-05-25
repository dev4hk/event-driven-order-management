package org.example.shippingservice.query;

import lombok.Value;

import java.util.UUID;

@Value
public class GetShippingByOrderIdQuery {
    private final UUID orderId;
}