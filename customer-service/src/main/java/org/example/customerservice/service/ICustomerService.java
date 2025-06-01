package org.example.customerservice.service;

import org.example.customerservice.entity.Customer;

import java.util.List;
import java.util.UUID;

public interface ICustomerService {
    void createCustomer(Customer customer);
    boolean updateCustomer(Customer customer);
    void deleteCustomer(UUID customerId);

    List<Customer> getAllCustomers();
    Customer getCustomerById(UUID customerId);

    void approveCredit(UUID customerId);
}
