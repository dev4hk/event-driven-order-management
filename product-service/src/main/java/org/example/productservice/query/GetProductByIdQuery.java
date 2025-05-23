package org.example.productservice.query;

import lombok.Value;

import java.util.UUID;

@Value
public class GetProductByIdQuery {
    private final UUID productId;
}
