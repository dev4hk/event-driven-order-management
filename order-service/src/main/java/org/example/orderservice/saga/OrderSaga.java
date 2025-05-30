package org.example.orderservice.saga;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.example.common.commands.ValidateCustomerCommand;
import org.example.common.dto.OrderItemDto;
import org.example.common.events.CustomerValidatedEvent;
import org.example.common.events.CustomerValidationFailedEvent;
import org.example.common.events.OrderCreatedEvent;
import org.example.orderservice.command.CancelOrderCommand;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.*;

@Saga
@Slf4j
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private UUID orderId;
    private UUID customerId;
    private UUID paymentId;
    private UUID shippingId;

    private List<OrderItemDto> items;
    private Map<UUID, Integer> reservedProducts = new HashMap<>();
    private Set<UUID> failedProducts = new HashSet<>();

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        log.info("[Saga] Received OrderCreatedEvent for order {}", event.getOrderId());

        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.items = event.getItems();

        ValidateCustomerCommand command = ValidateCustomerCommand.builder()
                .customerId(customerId)
                .build();

        commandGateway.send(command, new CommandCallback<ValidateCustomerCommand, Object>() {
            @Override
            public void onResult(@Nonnull CommandMessage<? extends ValidateCustomerCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    Throwable exception = commandResultMessage.exceptionResult();
                    log.error("[Saga] Failed to dispatch ValidateCustomerCommand for customer {}: {}", customerId, exception.getMessage());
                    CancelOrderCommand cancelOrderCommand = CancelOrderCommand.builder()
                            .orderId(orderId)
                            .customerId(customerId)
                            .reason("Customer validation dispatch failed: " + exception.getMessage())
                            .build();
                    commandGateway.send(cancelOrderCommand);
                }
            }
        });

    }

    @SagaEventHandler(associationProperty = "customerId")
    public void on(CustomerValidatedEvent event) {
        log.info("[Saga] Customer {} validated", event.getCustomerId());
        // Todo: reserve products
    }

    @SagaEventHandler(associationProperty = "customerId")
    public void on(CustomerValidationFailedEvent event) {
        log.warn("[Saga] Customer {} validation failed: {}", event.getCustomerId(), event.getReason());
        CancelOrderCommand cancelOrderCommand = CancelOrderCommand.builder()
                .orderId(orderId)
                .customerId(customerId)
                .reason("Customer validation failed: " + event.getReason())
                .build();
        commandGateway.send(cancelOrderCommand);
    }

}
