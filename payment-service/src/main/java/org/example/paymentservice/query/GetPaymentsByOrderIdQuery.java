package org.example.paymentservice.query;

import lombok.Value;

import java.util.UUID;

@Value
public class GetPaymentsByOrderIdQuery {
    private final UUID orderId;
}
