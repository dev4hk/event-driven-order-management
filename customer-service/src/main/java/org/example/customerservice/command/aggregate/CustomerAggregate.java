package org.example.customerservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.commands.ValidateCustomerCommand;
import org.example.common.events.*;
import org.example.customerservice.command.ApproveCustomerCreditCommand;
import org.example.customerservice.command.CreateCustomerCommand;
import org.example.customerservice.command.DeactivateCustomerCommand;
import org.example.customerservice.command.UpdateCustomerCommand;
import org.example.customerservice.command.events.CustomerCreditApprovedEvent;
import org.example.customerservice.exception.InvalidCustomerStateException;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
public class CustomerAggregate {

    @AggregateIdentifier
    private UUID customerId;
    private String name;
    private String email;
    private boolean active;
    private boolean creditApproved;

    @CommandHandler
    public CustomerAggregate(CreateCustomerCommand command) {
        CustomerCreatedEvent customerCreatedEvent = new CustomerCreatedEvent();
        BeanUtils.copyProperties(command, customerCreatedEvent);
        customerCreatedEvent.setCreditApproved(false);
        customerCreatedEvent.setActive(true);
        apply(customerCreatedEvent);
    }

    @EventSourcingHandler
    public void on(CustomerCreatedEvent event) {
        this.customerId = event.getCustomerId();
        this.name = event.getName();
        this.email = event.getEmail();
        this.active = event.isActive();
        this.creditApproved = event.isCreditApproved();
    }

    @CommandHandler
    public void handle(UpdateCustomerCommand command) {
        CustomerUpdatedEvent customerUpdatedEvent = new CustomerUpdatedEvent();
        BeanUtils.copyProperties(command, customerUpdatedEvent);
        apply(customerUpdatedEvent);
    }

    @EventSourcingHandler
    public void on(CustomerUpdatedEvent event) {
        this.name = event.getName();
        this.email = event.getEmail();
        this.active = event.isActive();
        this.creditApproved = event.isCreditApproved();
    }

    @CommandHandler
    public void handle(DeactivateCustomerCommand command) {
        CustomerDeactivatedEvent customerDeletedEvent = new CustomerDeactivatedEvent();
        BeanUtils.copyProperties(command, customerDeletedEvent);
        apply(customerDeletedEvent);
    }

    @EventSourcingHandler
    public void on(CustomerDeactivatedEvent event) {
        this.active = false;
    }

    @CommandHandler
    public void handle(ValidateCustomerCommand command) {
        if(!this.active) {
            throw new InvalidCustomerStateException("Customer is not active");
        } else if(!this.creditApproved) {
            throw new InvalidCustomerStateException("Customer credit is not approved");
        } else {
            CustomerValidatedEvent customerValidatedEvent = CustomerValidatedEvent.builder()
                    .customerId(command.getCustomerId())
                    .orderId(command.getOrderId())
                    .customerName(this.name)
                    .customerEmail(this.email)
                    .build();
            apply(customerValidatedEvent);
        }
    }

    @EventSourcingHandler
    public void on(CustomerValidatedEvent event) {
    }

    @CommandHandler
    public void handle(ApproveCustomerCreditCommand command) {
        CustomerCreditApprovedEvent customerCreditApprovedEvent = new CustomerCreditApprovedEvent();
        BeanUtils.copyProperties(command, customerCreditApprovedEvent);
        apply(customerCreditApprovedEvent);
    }

    @EventSourcingHandler
    public void on(CustomerCreditApprovedEvent event) {
        this.creditApproved = true;
    }
}
