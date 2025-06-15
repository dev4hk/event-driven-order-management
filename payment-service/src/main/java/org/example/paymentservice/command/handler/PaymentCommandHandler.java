package org.example.paymentservice.command.handler;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.example.common.commands.FailPaymentCommand;
import org.example.common.commands.InitiatePaymentCommand;
import org.example.common.events.PaymentFailedEvent;
import org.example.common.events.PaymentInitiatedEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.axonframework.commandhandling.CommandHandler;

@Component
@RequiredArgsConstructor
public class PaymentCommandHandler {

    private final EventGateway eventGateway;

    @CommandHandler
    public void handle(InitiatePaymentCommand command) {
        PaymentInitiatedEvent paymentInitiatedEvent = new PaymentInitiatedEvent();
        BeanUtils.copyProperties(command, paymentInitiatedEvent);
        eventGateway.publish(paymentInitiatedEvent);
    }

    @CommandHandler
    public void handle(FailPaymentCommand command) {
        PaymentFailedEvent paymentFailedEvent = PaymentFailedEvent.builder()
                .orderId(command.getOrderId())
                .message(command.getMessage())
                .build();
        eventGateway.publish(paymentFailedEvent);
    }

}
