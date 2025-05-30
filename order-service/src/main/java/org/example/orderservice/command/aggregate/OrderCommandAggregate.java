package org.example.orderservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.constants.OrderStatus;
import org.example.common.dto.OrderItemDto;
import org.example.common.events.OrderCancelledEvent;
import org.example.common.events.OrderCompletedEvent;
import org.example.common.events.OrderCreatedEvent;
import org.example.common.events.OrderUpdatedEvent;
import org.example.orderservice.exception.OrderLifecycleViolationException;
import org.example.orderservice.command.CancelOrderCommand;
import org.example.orderservice.command.CompleteOrderCommand;
import org.example.orderservice.command.CreateOrderCommand;
import org.example.orderservice.command.UpdateOrderCommand;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Aggregate
@NoArgsConstructor
public class OrderCommandAggregate {

    @AggregateIdentifier
    private UUID orderId;
    private UUID customerId;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String reason;

    @CommandHandler
    public OrderCommandAggregate(CreateOrderCommand command) {
        OrderCreatedEvent event = new OrderCreatedEvent();
        BeanUtils.copyProperties(command, event);
        event.setStatus(OrderStatus.CREATED);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.items = event.getItems();
        this.totalAmount = event.getTotalAmount();
        this.status = event.getStatus();
    }

    @CommandHandler
    public void handle(UpdateOrderCommand command) {
        OrderUpdatedEvent event = new OrderUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        event.setStatus(OrderStatus.UPDATED);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderUpdatedEvent event) {
        this.customerId = event.getCustomerId();
        this.items = event.getItems();
        this.totalAmount = event.getTotalAmount();
        this.status = event.getStatus();
    }

    @CommandHandler
    public void handle(CancelOrderCommand command) {
        OrderCancelledEvent event = new OrderCancelledEvent();
        event.setStatus(OrderStatus.CANCELLED);
        event.setReason(command.getReason());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.status = OrderStatus.CANCELLED;
        this.reason = event.getReason();
    }

    @CommandHandler
    public void handle(CompleteOrderCommand command) {
        if (this.status == OrderStatus.CANCELLED) {
            throw new OrderLifecycleViolationException("Cannot complete a cancelled order.");
        }
        OrderCompletedEvent event = new OrderCompletedEvent();
        event.setStatus(OrderStatus.COMPLETED);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent event) {
        this.status = event.getStatus();
    }

}
