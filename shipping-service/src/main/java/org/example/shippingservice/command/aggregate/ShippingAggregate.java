package org.example.shippingservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.commands.FailShippingCommand;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.ShippingDetails;
import org.example.common.events.ShippingDeliveredEvent;
import org.example.common.events.ShippingFailedEvent;
import org.example.common.events.ShippingProcessedEvent;
import org.example.shippingservice.command.DeliverShippingCommand;
import org.example.common.commands.ProcessShippingCommand;
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
    private ShippingDetails shippingDetails;
    private ShippingStatus shippingStatus;
    private String message;
    private LocalDateTime updatedAt;

    @CommandHandler
    public ShippingAggregate(ProcessShippingCommand command) {
        ShippingProcessedEvent event = new ShippingProcessedEvent();
        BeanUtils.copyProperties(command, event);
        event.setShippingStatus(ShippingStatus.SHIPPED);
        event.setUpdatedAt(LocalDateTime.now());
        event.setMessage("Shipping has been processed.");
        apply(event);
    }

    @EventSourcingHandler
    public void on(ShippingProcessedEvent event) {
        this.shippingId = event.getShippingId();
        this.orderId = event.getOrderId();
        this.shippingDetails = event.getShippingDetails();
        this.shippingStatus = event.getShippingStatus();
        this.message = event.getMessage();
        this.updatedAt = event.getUpdatedAt();
    }

    @CommandHandler
    public void handle(DeliverShippingCommand command) {
        if (this.shippingStatus != ShippingStatus.SHIPPED) {
            throw new InvalidShippingStateException("Cannot update shipping status as DELIVERED if it is not SHIPPED.");
        }
        ShippingDeliveredEvent event = new ShippingDeliveredEvent();
        BeanUtils.copyProperties(command, event);
        event.setOrderId(orderId);
        event.setShippingStatus(ShippingStatus.DELIVERED);
        event.setUpdatedAt(LocalDateTime.now());
        event.setMessage("Shipping delivered");
        apply(event);
    }

    @EventSourcingHandler
    public void on(ShippingDeliveredEvent event) {
        this.shippingStatus = event.getShippingStatus();
        this.updatedAt = event.getUpdatedAt();
        this.message = event.getMessage();
    }

    @CommandHandler
    public void handle(FailShippingCommand command) {
        ShippingFailedEvent shippingFailedEvent = ShippingFailedEvent.builder()
                .orderId(command.getOrderId())
                .shippingId(command.getShippingId())
                .shippingStatus(ShippingStatus.FAILED)
                .updatedAt(LocalDateTime.now())
                .message(command.getMessage())
                .build();
        apply(shippingFailedEvent);
    }

    @EventSourcingHandler
    public void on(ShippingFailedEvent event) {
        this.shippingStatus = event.getShippingStatus();
        this.updatedAt = event.getUpdatedAt();
        this.message = event.getMessage();
    }

}
