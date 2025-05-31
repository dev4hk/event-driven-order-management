package org.example.shippingservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.constants.ShippingStatus;
import org.example.common.events.ShippingCreatedEvent;
import org.example.common.events.ShippingProcessedEvent;
import org.example.common.commands.CreateShippingCommand;
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
    private UUID paymentId;
    private ShippingStatus status;

    @CommandHandler
    public ShippingAggregate(CreateShippingCommand command) {

        ShippingCreatedEvent event = new ShippingCreatedEvent();
        BeanUtils.copyProperties(command, event);
        event.setStatus(ShippingStatus.PENDING);
        event.setShippedAt(LocalDateTime.now());
        apply(event);
    }

    @EventSourcingHandler
    public void on(ShippingCreatedEvent event) {
        this.shippingId = event.getShippingId();
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.paymentId = event.getPaymentId();
        this.status = event.getStatus();
    }

    @CommandHandler
    public void handle(ProcessShippingCommand command) {
        if (this.status == ShippingStatus.DELIVERED) {
            throw new InvalidShippingStateException("Cannot update shipping status after it has been delivered.");
        }
        ShippingProcessedEvent event = new ShippingProcessedEvent();
        BeanUtils.copyProperties(command, event);
        event.setUpdatedAt(LocalDateTime.now());
        apply(event);
    }

    @EventSourcingHandler
    public void on(ShippingProcessedEvent event) {
        this.status = event.getNewStatus();
    }

}
