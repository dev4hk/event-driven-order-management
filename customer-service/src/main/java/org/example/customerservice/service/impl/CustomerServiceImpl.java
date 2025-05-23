package org.example.customerservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.customerservice.entity.Customer;
import org.example.customerservice.repository.CustomerRepository;
import org.example.customerservice.service.ICustomerService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public void createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new ResourceAlreadyExistsException("Customer with email " + customer.getEmail() + " already exists");
        }
        customerRepository.save(customer);
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        return customerRepository.findById(customer.getCustomerId())
                .map(existing -> {
                    existing.setName(customer.getName());
                    existing.setEmail(customer.getEmail());
                    existing.setActive(customer.isActive());
                    existing.setCreditApproved(customer.isCreditApproved());
                    customerRepository.save(existing);
                    return true;
                })
                .orElse(false);
    }
}
