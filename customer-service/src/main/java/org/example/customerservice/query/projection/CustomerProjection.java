package org.example.customerservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.example.customerservice.events.CustomerCreatedEvent;
import org.example.customerservice.events.CustomerDeactivatedEvent;
import org.example.customerservice.events.CustomerUpdatedEvent;
import org.example.customerservice.events.CustomerCreditApprovedEvent;
import org.example.customerservice.entity.Customer;
import org.example.customerservice.service.ICustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("customer-group")
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

    @EventHandler
    public void on(CustomerDeactivatedEvent event) {
        iCustomerService.deactivateCustomer(event.getCustomerId());
    }

    @EventHandler
    public void on(CustomerCreditApprovedEvent event) {
        iCustomerService.approveCredit(event.getCustomerId());
    }
}
