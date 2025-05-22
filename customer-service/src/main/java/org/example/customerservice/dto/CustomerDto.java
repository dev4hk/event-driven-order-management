package org.example.customerservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CustomerDto {
    private UUID customerId;
    private String name;
    private boolean active;
    private boolean creditApproved;
}

