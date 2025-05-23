package org.example.customerservice.query.handler;

import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.example.customerservice.dto.CustomerResponseDto;
import org.example.customerservice.entity.Customer;
import org.example.customerservice.query.GetAllCustomersQuery;
import org.example.customerservice.query.GetCustomerByIdQuery;
import org.example.customerservice.service.ICustomerService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerQueryHandler {

    private final ICustomerService customerService;

    @QueryHandler
    public List<CustomerResponseDto> getAllCustomers(GetAllCustomersQuery query) {
        return customerService.getAllCustomers().stream()
                .map(CustomerResponseDto::new)
                .collect(Collectors.toList());
    }

    @QueryHandler
    public CustomerResponseDto getCustomerById(GetCustomerByIdQuery query) {
        Customer customer = customerService.getCustomerById(query.customerId());
        return new CustomerResponseDto(customer);
    }
}