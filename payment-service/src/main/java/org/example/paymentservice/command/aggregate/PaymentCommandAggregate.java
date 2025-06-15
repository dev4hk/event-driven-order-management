package org.example.paymentservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.commands.CancelPaymentCommand;
import org.example.common.commands.ProcessPaymentCommand;
import org.example.common.commands.RollBackPaymentStatusCommand;
import org.example.common.constants.PaymentStatus;
import org.example.common.events.PaymentCancelledEvent;
import org.example.common.events.PaymentProcessedEvent;
import org.example.common.events.PaymentStatusRolledBackEvent;
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
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private String message;
    private LocalDateTime updatedAt;

    @CommandHandler
    public PaymentCommandAggregate(ProcessPaymentCommand command) {
        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .paymentId(command.getPaymentId())
                .orderId(command.getOrderId())
                .customerId(command.getCustomerId())
                .totalAmount(command.getTotalAmount())
                .paymentStatus(PaymentStatus.COMPLETED)
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
        this.totalAmount = event.getTotalAmount();
        this.paymentStatus = event.getPaymentStatus();
        this.message = event.getMessage();
        this.updatedAt = event.getUpdatedAt();
    }

    @CommandHandler
    public void handle(CancelPaymentCommand command) {
        if (!this.paymentStatus.equals(PaymentStatus.COMPLETED)) {
            throw new InvalidPaymentStateException("Cannot cancel a payment that is not completed.");
        }
        PaymentCancelledEvent event = new PaymentCancelledEvent();
        BeanUtils.copyProperties(command, event);
        event.setPaymentStatus(PaymentStatus.CANCELLED);
        event.setCancelledAt(LocalDateTime.now());
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PaymentCancelledEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.totalAmount = event.getAmount();
        this.paymentStatus = event.getPaymentStatus();
        this.message = event.getMessage();
        this.updatedAt = event.getCancelledAt();
    }

    @CommandHandler
    public void on(RollBackPaymentStatusCommand command) {
        PaymentStatusRolledBackEvent event = PaymentStatusRolledBackEvent.builder()
                .paymentId(command.getPaymentId())
                .orderId(command.getOrderId())
                .paymentStatus(command.getPaymentStatus())
                .build();
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PaymentStatusRolledBackEvent event) {
        this.paymentStatus = event.getPaymentStatus();
    }

}
