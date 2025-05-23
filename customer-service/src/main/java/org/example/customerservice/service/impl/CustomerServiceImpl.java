package org.example.customerservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.customerservice.entity.Customer;
import org.example.customerservice.repository.CustomerRepository;
import org.example.customerservice.service.ICustomerService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public void createCustomer(Customer customer) {
        Optional<Customer> existing = customerRepository.findByEmailAndActive(customer.getEmail(), true);
        if (existing.isPresent()) {
            throw new ResourceAlreadyExistsException("Customer with email " + customer.getEmail() + " already exists");
        }
        customerRepository.save(customer);
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        Customer existing = customerRepository.findByEmailAndActive(customer.getEmail(), true)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with email " + customer.getEmail() + " not found"));
        existing.setName(customer.getName());
        existing.setCreditApproved(customer.isCreditApproved());
        customerRepository.save(existing);
        return true;
    }

    @Override
    public void deleteCustomer(UUID customerId) {
        Customer existing = customerRepository.findByCustomerIdAndActive(customerId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + customerId + " not found"));
        existing.setActive(false);
        customerRepository.save(existing);
    }
}
