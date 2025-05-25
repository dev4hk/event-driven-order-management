package org.example.shippingservice.query;

import lombok.Value;

import java.util.UUID;

@Value
public class GetShippingByIdQuery {
    private final UUID shippingId;
}