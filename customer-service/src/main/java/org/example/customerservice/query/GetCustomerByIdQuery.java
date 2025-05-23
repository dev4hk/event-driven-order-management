package org.example.customerservice.query;

import lombok.Value;

import java.util.UUID;

@Value
public class GetCustomerByIdQuery {
    private final UUID customerId;
}
