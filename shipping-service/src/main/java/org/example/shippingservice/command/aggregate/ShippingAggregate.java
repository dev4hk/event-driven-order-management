package org.example.shippingservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.commands.CancelShippingCommand;
import org.example.common.constants.ShippingStatus;
import org.example.common.events.*;
import org.example.common.commands.InitiateShippingCommand;
import org.example.shippingservice.command.DeliverShippingCommand;
import org.example.shippingservice.command.ProcessShippingCommand;
import org.example.shippingservice.exception.InvalidShippingStateException;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
public class ShippingAggregate {

    @AggregateIdentifier
    private UUID shippingId;
    private UUID orderId;
    private UUID customerId;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String customerName;
    private String customerEmail;
    private ShippingStatus status;
    private String message;
    private LocalDateTime updatedAt;

    @CommandHandler
    public ShippingAggregate(InitiateShippingCommand command) {

        ShippingInitiatedEvent event = new ShippingInitiatedEvent();
        BeanUtils.copyProperties(command, event);
        event.setStatus(ShippingStatus.INITIATED);
        event.setUpdatedAt(LocalDateTime.now());
        event.setMessage("Shipping initiated");
        apply(event);
    }

    @EventSourcingHandler
    public void on(ShippingInitiatedEvent event) {
        this.shippingId = event.getShippingId();
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.address = event.getAddress();
        this.city = event.getCity();
        this.state = event.getState();
        this.zipCode = event.getZipCode();
        this.customerName = event.getCustomerName();
        this.customerEmail = event.getCustomerEmail();
        this.status = event.getStatus();
        this.updatedAt = event.getUpdatedAt();
        this.message = event.getMessage();
    }

    @CommandHandler
    public void handle(ProcessShippingCommand command) {
        if (this.status == ShippingStatus.SHIPPED || this.status == ShippingStatus.DELIVERED) {
            throw new InvalidShippingStateException("Cannot update shipping status after it has been shipped or delivered.");
        }
        ShippingProcessedEvent event = new ShippingProcessedEvent();
        BeanUtils.copyProperties(command, event);
        event.setOrderId(orderId);
        event.setUpdatedAt(LocalDateTime.now());
        event.setStatus(ShippingStatus.SHIPPED);
        apply(event).andThen(() -> {
            ShippingDataUpdatedEvent shippingDataUpdatedEvent = new ShippingDataUpdatedEvent();
            BeanUtils.copyProperties(event, shippingDataUpdatedEvent);
            apply(shippingDataUpdatedEvent);
        });
    }

    @EventSourcingHandler
    public void on(ShippingProcessedEvent event) {
        this.status = event.getStatus();
        this.updatedAt = event.getUpdatedAt();
    }

    @CommandHandler
    public void handle(DeliverShippingCommand command) {
        if (this.status != ShippingStatus.SHIPPED) {
            throw new InvalidShippingStateException("Cannot update shipping status as DELIVERED if it is not SHIPPED.");
        }
        ShippingDeliveredEvent event = new ShippingDeliveredEvent();
        BeanUtils.copyProperties(command, event);
        event.setStatus(ShippingStatus.DELIVERED);
        event.setOrderId(orderId);
        event.setUpdatedAt(LocalDateTime.now());
        apply(event);
    }

    @EventSourcingHandler
    public void on(ShippingDeliveredEvent event) {
        this.status = event.getStatus();
        this.updatedAt = event.getUpdatedAt();
    }

    @CommandHandler
    public void handle(CancelShippingCommand command) {
        if (this.status != ShippingStatus.INITIATED) {
            throw new InvalidShippingStateException("Cannot update shipping status as CANCELLED if it is not PENDING.");
        }
        ShippingCancelledEvent event = new ShippingCancelledEvent();
        BeanUtils.copyProperties(command, event);
        event.setStatus(ShippingStatus.CANCELLED);
        event.setCancelledAt(LocalDateTime.now());
        apply(event);
    }

    @EventSourcingHandler
    public void on(ShippingCancelledEvent event) {
        this.status = event.getStatus();
        this.message = event.getMessage();
        this.updatedAt = event.getCancelledAt();
    }
}
