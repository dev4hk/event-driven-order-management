package org.example.paymentservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.commands.CancelPaymentCommand;
import org.example.common.commands.InitiatePaymentCommand;
import org.example.common.constants.PaymentStatus;
import org.example.common.events.PaymentCancelledEvent;
import org.example.common.events.PaymentInitiatedEvent;
import org.example.common.events.PaymentProcessedEvent;
import org.example.common.commands.ProcessPaymentCommand;
import org.example.paymentservice.exception.InvalidPaymentStateException;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Aggregate
@NoArgsConstructor
public class PaymentCommandAggregate {

    @AggregateIdentifier
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String message;
    private LocalDateTime updatedAt;

    private String customerName;
    private String customerEmail;
    private String address;
    private String city;
    private String state;
    private String zipCode;

    @CommandHandler
    public PaymentCommandAggregate(InitiatePaymentCommand command) {
        PaymentInitiatedEvent event = new PaymentInitiatedEvent();
        BeanUtils.copyProperties(command, event);
        event.setStatus(PaymentStatus.INITIATED);
        event.setUpdatedAt(LocalDateTime.now());
        event.setMessage("Payment initiated");
        AggregateLifecycle.apply(event);

    }

    @EventSourcingHandler
    public void on(PaymentInitiatedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.amount = event.getTotalAmount();
        this.status = event.getStatus();
        this.updatedAt = event.getUpdatedAt();
        this.customerName = event.getCustomerName();
        this.customerEmail = event.getCustomerEmail();
        this.address = event.getAddress();
        this.city = event.getCity();
        this.state = event.getState();
        this.zipCode = event.getZipCode();
        this.message = event.getMessage();
    }

    @CommandHandler
    public void handle(CancelPaymentCommand command) {
        if (!this.status.equals(PaymentStatus.COMPLETED)) {
            throw new InvalidPaymentStateException("Cannot cancel a payment that is not completed.");
        }
        PaymentCancelledEvent event = new PaymentCancelledEvent();
        BeanUtils.copyProperties(command, event);
        event.setStatus(PaymentStatus.CANCELLED);
        event.setCancelledAt(LocalDateTime.now());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PaymentCancelledEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.amount = event.getAmount();
        this.status = event.getStatus();
        this.message = event.getMessage();
        this.updatedAt = event.getCancelledAt();
    }

    @CommandHandler
    public void handle(ProcessPaymentCommand command) {
        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .paymentId(command.getPaymentId())
                .orderId(command.getOrderId())
                .customerId(command.getCustomerId())
                .totalAmount(command.getTotalAmount())
                .status(PaymentStatus.COMPLETED)
                .message("Payment processed")
                .updatedAt(LocalDateTime.now())
                .build();
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.amount = event.getTotalAmount();
        this.status = event.getStatus();
        this.updatedAt = event.getUpdatedAt();
    }

}
