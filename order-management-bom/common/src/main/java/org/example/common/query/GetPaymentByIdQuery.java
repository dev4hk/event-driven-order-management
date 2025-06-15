package org.example.common.query;

import lombok.Value;

import java.util.UUID;

@Value
public class GetPaymentByIdQuery {
    private final UUID paymentId;
}