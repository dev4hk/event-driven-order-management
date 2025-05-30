package org.example.orderservice.saga;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.example.common.commands.ReleaseProductReservationCommand;
import org.example.common.commands.ReserveProductCommand;
import org.example.common.commands.ValidateCustomerCommand;
import org.example.common.dto.OrderItemDto;
import org.example.common.events.*;
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
    private boolean isOrderCancelled = false;

    private List<OrderItemDto> items;
    private Map<UUID, Integer> reservedProducts = new HashMap<>();
    private Set<UUID> failedProducts = new HashSet<>();
    private Set<UUID> releasedProducts = new HashSet<>();

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        log.info("[Saga] Received OrderCreatedEvent for order {}", event.getOrderId());

        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.items = event.getItems();

        if (items.isEmpty()) {
            cancelOrder("No products in order");
            return;
        }

        ValidateCustomerCommand command = ValidateCustomerCommand.builder()
                .customerId(customerId)
                .build();

        commandGateway.send(command, new CommandCallback<ValidateCustomerCommand, Object>() {
            @Override
            public void onResult(@Nonnull CommandMessage<? extends ValidateCustomerCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    Throwable exception = commandResultMessage.exceptionResult();
                    log.error("[Saga] Failed to dispatch ValidateCustomerCommand for customer {}: {}", customerId, exception.getMessage());
                    cancelOrder("Customer validation dispatch failed: " + exception.getMessage());

                }
            }
        });

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(CustomerValidationFailedEvent event) {
        log.warn("[Saga] Customer {} validation failed: {}", event.getCustomerId(), event.getReason());
        cancelOrder("Customer validation failed: " + event.getReason());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(CustomerValidatedEvent event) {
        log.info("[Saga] Received CustomerValidatedEvent for customerId {}. Reserving {} products", event.getCustomerId(), items.size());

        for (OrderItemDto item : this.items) {
            ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                    .orderId(orderId)
                    .productId(item.getProductId())
                    .customerId(customerId)
                    .quantity(item.getQuantity())
                    .build();
            commandGateway.send(reserveProductCommand);
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ProductReservedEvent command) {
        log.info("[Saga] Received ProductReservedEvent for productId {}", command.getProductId());
        this.reservedProducts.put(command.getProductId(), command.getQuantity());
        checkReservationCompletion();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ProductReservationFailedEvent command) {
        log.info("[Saga] Received ProductReservationFailedEvent for productId {}", command.getProductId());
        this.failedProducts.add(command.getProductId());
        checkReservationCompletion();
    }

    private void checkReservationCompletion() {
        int totalProcessed = this.reservedProducts.size() + this.failedProducts.size();

        if (totalProcessed < this.items.size()) {
            return;
        }

        if (!this.failedProducts.isEmpty()) {
            log.warn("[Saga] One or more products failed to reserve. Triggering compensation...");
            for (UUID productId : this.reservedProducts.keySet()) {
                ReleaseProductReservationCommand releaseProductReservationCommand = ReleaseProductReservationCommand.builder()
                        .orderId(orderId)
                        .productId(productId)
                        .customerId(customerId)
                        .quantity(this.reservedProducts.get(productId))
                        .build();
                commandGateway.send(releaseProductReservationCommand);
            }
        }
        else {
            log.info("[Saga] All products reserved. Triggering payment...");
            this.paymentId = UUID.randomUUID();
            // TODO: trigger payment
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ProductReservationReleasedEvent command) {
        log.info("[Saga] Received ProductReservationReleasedEvent for productId {}", command.getProductId());
        this.releasedProducts.add(command.getProductId());
        if(this.releasedProducts.containsAll(this.reservedProducts.keySet())) {
            log.info("[Saga] All product reservations released. Sending CancelOrderCommand.");
            cancelOrder("One or more product reservations failed");
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void on(OrderCancelledEvent event) {
        log.info("[Saga] Received OrderCancelledEvent for orderId {}", event.getOrderId());
    }

    private void cancelOrder(String reason) {
        if (!isOrderCancelled) {
            isOrderCancelled = true;
            CancelOrderCommand cancelOrderCommand = CancelOrderCommand.builder()
                    .orderId(orderId)
                    .customerId(customerId)
                    .reason(reason)
                    .build();
            commandGateway.send(cancelOrderCommand);
        }
    }


}
