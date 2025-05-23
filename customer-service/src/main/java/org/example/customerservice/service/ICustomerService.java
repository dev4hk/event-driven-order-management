package org.example.customerservice.service;

import org.example.customerservice.entity.Customer;

import java.util.UUID;

public interface ICustomerService {
    void createCustomer(Customer customer);
    boolean updateCustomer(Customer customer);
}
