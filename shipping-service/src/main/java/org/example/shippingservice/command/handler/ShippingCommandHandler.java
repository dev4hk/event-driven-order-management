package org.example.shippingservice.command.handler;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.example.shippingservice.command.InitiateShippingCommand;
import org.example.common.events.ShippingInitiatedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShippingCommandHandler {

    private final EventGateway eventGateway;

    @CommandHandler
    public void handle(InitiateShippingCommand command) {
        ShippingInitiatedEvent shippingInitiatedEvent = ShippingInitiatedEvent.builder()
                .shippingId(command.getShippingId())
                .orderId(command.getOrderId())
                .shippingDetails(command.getShippingDetails())
                .build();
        eventGateway.publish(shippingInitiatedEvent);
    }

}
