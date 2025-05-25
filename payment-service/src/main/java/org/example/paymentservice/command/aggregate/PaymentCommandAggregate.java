package org.example.paymentservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.constants.PaymentStatus;
import org.example.common.events.PaymentCreatedEvent;
import org.example.paymentservice.command.CreatePaymentCommand;
import org.example.paymentservice.exception.InvalidPaymentDataException;

import java.math.BigDecimal;
import java.util.UUID;

@Aggregate
@NoArgsConstructor
public class PaymentCommandAggregate {

    @AggregateIdentifier
    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentStatus status;

    @CommandHandler
    public PaymentCommandAggregate(CreatePaymentCommand command) {
        if (command.getAmount() == null || command.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentDataException("Payment amount must be greater than zero.");
        }

        PaymentCreatedEvent event = PaymentCreatedEvent.builder()
                .paymentId(command.getPaymentId())
                .orderId(command.getOrderId())
                .amount(command.getAmount())
                .status(PaymentStatus.COMPLETED)
                .build();

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PaymentCreatedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.amount = event.getAmount();
        this.status = event.getStatus();
    }
}
