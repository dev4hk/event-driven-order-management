package org.example.customerservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.customerservice.entity.Customer;
import org.example.customerservice.exception.InvalidCustomerStateException;
import org.example.customerservice.repository.CustomerRepository;
import org.example.customerservice.service.ICustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
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
        Customer existing = customerRepository.findByCustomerIdAndActive(customer.getCustomerId(), true)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + customer.getCustomerId() + " not found"));
        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        existing.setCreditApproved(customer.isCreditApproved());
        customerRepository.save(existing);
        return true;
    }

    @Override
    public void deactivateCustomer(UUID customerId) {
        Customer existing = customerRepository.findByCustomerIdAndActive(customerId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + customerId + " not found"));
        existing.setActive(false);
        customerRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAllByActive(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(UUID customerId) {
        return customerRepository.findByCustomerIdAndActive(customerId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + customerId + " not found"));
    }

    @Override
    public void approveCredit(UUID customerId) {
        Customer customer = getCustomerById(customerId);
        if (customer.isCreditApproved()) {
            throw new InvalidCustomerStateException("Customer is already approved");
        }
        customer.setCreditApproved(true);
        customerRepository.save(customer);
    }
}
