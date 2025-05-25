package org.example.orderservice.query;

import lombok.Value;

import java.util.UUID;

@Value
public class GetOrderByIdQuery {
    private final UUID orderId;
}
