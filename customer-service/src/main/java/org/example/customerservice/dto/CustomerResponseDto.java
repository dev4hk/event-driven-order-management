package org.example.customerservice.dto;

import lombok.Data;
import org.example.customerservice.entity.Customer;

import java.util.UUID;

@Data
public class CustomerResponseDto {
    private UUID customerId;
    private String name;
    private String email;

    public CustomerResponseDto(Customer customer) {
        this.customerId = customer.getCustomerId();
        this.name = customer.getName();
        this.email = customer.getEmail();
    }

}