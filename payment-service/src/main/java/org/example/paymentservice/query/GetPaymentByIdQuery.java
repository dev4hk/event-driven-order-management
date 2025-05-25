package org.example.paymentservice.query;

import lombok.Value;

import java.util.UUID;

@Value
public class GetPaymentByIdQuery {
    private final UUID paymentId;
}