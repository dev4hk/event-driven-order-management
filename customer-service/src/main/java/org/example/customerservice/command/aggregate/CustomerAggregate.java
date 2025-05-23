package org.example.customerservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.events.CustomerCreatedEvent;
import org.example.common.events.CustomerUpdatedEvent;
import org.example.customerservice.command.CreateCustomerCommand;
import org.example.customerservice.command.DeleteCustomerCommand;
import org.example.customerservice.command.UpdateCustomerCommand;
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
    public void handle(DeleteCustomerCommand command) {
        // todo: implement delete logic
    }
}
