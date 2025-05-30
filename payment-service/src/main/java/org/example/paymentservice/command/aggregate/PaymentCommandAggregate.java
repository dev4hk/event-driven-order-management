package org.example.paymentservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.constants.PaymentStatus;
import org.example.common.events.PaymentProcessedEvent;
import org.example.common.commands.CreatePaymentCommand;
import org.example.common.events.PaymentFailedEvent;

import java.math.BigDecimal;
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
    private String reason;

    @CommandHandler
    public PaymentCommandAggregate(CreatePaymentCommand command) {
        if (command.getAmount() == null || command.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            PaymentFailedEvent event = PaymentFailedEvent.builder()
                    .paymentId(command.getPaymentId())
                    .orderId(command.getOrderId())
                    .customerId(command.getCustomerId())
                    .amount(command.getAmount())
                    .status(PaymentStatus.FAILED)
                    .reason("Invalid amount")
                    .build();
            AggregateLifecycle.apply(event);
        }
        else {
            PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                    .paymentId(command.getPaymentId())
                    .orderId(command.getOrderId())
                    .customerId(command.getCustomerId())
                    .amount(command.getAmount())
                    .status(PaymentStatus.COMPLETED)
                    .build();
            AggregateLifecycle.apply(event);
        }

    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.amount = event.getAmount();
        this.status = event.getStatus();
    }

    @EventSourcingHandler
    public void on(PaymentFailedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.amount = event.getAmount();
        this.status = event.getStatus();
        this.reason = event.getReason();
    }
}
