package org.example.common.query;

import lombok.Value;

import java.util.UUID;

@Value
public class GetShippingByIdQuery {
    private final UUID shippingId;
}