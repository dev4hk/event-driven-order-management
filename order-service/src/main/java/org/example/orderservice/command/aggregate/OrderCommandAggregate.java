package org.example.orderservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.commands.UpdatePaymentStatusCommand;
import org.example.common.constants.OrderStatus;
import org.example.common.constants.PaymentStatus;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.OrderItemDto;
import org.example.common.events.*;
import org.example.orderservice.command.*;
import org.example.orderservice.exception.InvalidOrderStateException;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Aggregate
@NoArgsConstructor
public class OrderCommandAggregate {

    @AggregateIdentifier
    private UUID orderId;
    private UUID customerId;
    private UUID paymentId;
    private UUID shippingId;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private ShippingStatus shippingStatus;
    private String customerName;
    private String customerEmail;
    private String message;
    private LocalDateTime updatedAt;

    @CommandHandler
    public OrderCommandAggregate(InitiateOrderCommand command) {
        OrderInitiatedEvent event = new OrderInitiatedEvent();
        BeanUtils.copyProperties(command, event);
        event.setOrderStatus(OrderStatus.INITIATED);
        event.setCreatedAt(LocalDateTime.now());
        event.setMessage("Order has been initiated.");
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderInitiatedEvent event) {
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.items = event.getItems();
        this.totalAmount = event.getTotalAmount();
        this.orderStatus = event.getOrderStatus();
        this.updatedAt = event.getCreatedAt();
        this.message = event.getMessage();
    }

    @CommandHandler
    public void handle(CancelOrderCommand command) {
        if (this.orderStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Order is already cancelled.");
        }
        OrderCancelledEvent event = new OrderCancelledEvent();
        BeanUtils.copyProperties(command, event);
        event.setOrderStatus(OrderStatus.CANCELLED);
        event.setCancelledAt(LocalDateTime.now());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.orderStatus = OrderStatus.CANCELLED;
        this.message = event.getMessage();
        this.updatedAt = event.getCancelledAt();
    }

    @CommandHandler
    public void handle(RequestOrderCancellationCommand command) {
        if (this.orderStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Order is already cancelled.");
        }

        OrderCancellationRequestedEvent event = new OrderCancellationRequestedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCancellationRequestedEvent event) {
        this.message = event.getMessage();
        this.updatedAt = event.getCancelledAt();
    }

    @CommandHandler
    public void handle(CompleteOrderCancellationCommand command) {
        OrderCancellationCompletedEvent event = new OrderCancellationCompletedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCancellationCompletedEvent event) {
        this.paymentStatus = event.getPaymentStatus();
        this.shippingStatus = event.getShippingStatus();
        this.message = event.getMessage();
        this.updatedAt = event.getCancelledAt();
    }

    @CommandHandler
    public void handle(UpdatePaymentStatusCommand command) {
        PaymentStatusUpdatedEvent event = new PaymentStatusUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PaymentStatusUpdatedEvent event) {
        this.paymentId = event.getPaymentId();
        this.paymentStatus = event.getPaymentStatus();
        this.message = event.getMessage();
        this.updatedAt = event.getUpdatedAt();
        this.customerName = event.getCustomerName();
        this.customerEmail = event.getCustomerEmail();
    }

    @CommandHandler
    public void handle(UpdateShippingStatusCommand command) {
        ShippingStatusUpdatedEvent event = new ShippingStatusUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ShippingStatusUpdatedEvent event) {
        this.shippingStatus = event.getShippingStatus();
        if(event.getShippingStatus() == ShippingStatus.DELIVERED) {
            this.orderStatus = OrderStatus.COMPLETED;
        }
        this.message = event.getMessage();
        this.updatedAt = event.getUpdatedAt();
    }

    @CommandHandler
    public void handle(UpdateCustomerInfoCommand command) {
        CustomerInfoUpdatedEvent event = new CustomerInfoUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        event.setMessage("Customer info has been updated.");
        event.setUpdatedAt(LocalDateTime.now());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(CustomerInfoUpdatedEvent event) {
        this.customerName = event.getCustomerName();
        this.customerEmail = event.getCustomerEmail();
        this.message = event.getMessage();
        this.updatedAt = event.getUpdatedAt();
    }

}
