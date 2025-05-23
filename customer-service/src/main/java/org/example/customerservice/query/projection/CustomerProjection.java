package org.example.customerservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.example.common.events.CustomerCreatedEvent;
import org.example.common.events.CustomerUpdatedEvent;
import org.example.customerservice.entity.Customer;
import org.example.customerservice.service.ICustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerProjection {

    private final ICustomerService iCustomerService;

    @EventHandler
    public void on(CustomerCreatedEvent event) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(event, customer);
        iCustomerService.createCustomer(customer);
    }

    @EventHandler
    public void on(CustomerUpdatedEvent event) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(event, customer);
        iCustomerService.updateCustomer(customer);
    }
}
