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
import org.example.common.commands.*;
import org.example.common.constants.OrderStatus;
import org.example.common.constants.PaymentStatus;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.OrderItemDto;
import org.example.common.events.*;
import org.example.orderservice.command.CancelOrderCommand;
import org.example.orderservice.command.CompleteOrderCommand;
import org.example.orderservice.command.UpdateShippingStatusCommand;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Saga
@Slf4j
public class CreateOrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private UUID orderId;
    private UUID customerId;
    private UUID paymentId;
    private UUID shippingId;
    private boolean isOrderCancelled = false;
    private LocalDateTime updatedAt;

    private List<OrderItemDto> items;
    private Map<UUID, Integer> reservedProducts = new HashMap<>();
    private Set<UUID> failedProducts = new HashSet<>();
    private Set<UUID> releasedProducts = new HashSet<>();

    private String address;
    private String city;
    private String state;
    private String zipCode;

    private BigDecimal totalAmount;
    private String customerName;
    private String customerEmail;

    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private ShippingStatus shippingStatus;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderInitiatedEvent event) {
        log.info("[Saga] Received OrderInitiatedEvent for order {}", event.getOrderId());

        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.items = event.getItems();
        this.updatedAt = event.getCreatedAt();
        this.totalAmount = event.getTotalAmount();
        this.address = event.getAddress();
        this.city = event.getCity();
        this.state = event.getState();
        this.zipCode = event.getZipCode();
        this.orderStatus = event.getOrderStatus();

        ValidateCustomerCommand command = ValidateCustomerCommand.builder()
                .customerId(customerId)
                .orderId(orderId)
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
    public void on(CustomerValidatedEvent event) {
        log.info("[Saga] Received CustomerValidatedEvent for customerId {}. Reserving {} products", event.getCustomerId(), items.size());
        this.customerName = event.getCustomerName();
        this.customerEmail = event.getCustomerEmail();

        for (OrderItemDto item : this.items) {
            ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                    .orderId(orderId)
                    .productId(item.getProductId())
                    .customerId(customerId)
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build();
            commandGateway.send(reserveProductCommand);
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ProductReservedEvent event) {
        log.info("[Saga] Received ProductReservedEvent for productId {}", event.getProductId());
        this.reservedProducts.put(event.getProductId(), event.getQuantity());
        checkReservationCompletion();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ProductReservationFailedEvent event) {
        log.info("[Saga] Received ProductReservationFailedEvent for productId {}", event.getProductId());
        this.failedProducts.add(event.getProductId());
        checkReservationCompletion();
    }

    private void checkReservationCompletion() {
        int totalProcessed = this.reservedProducts.size() + this.failedProducts.size();

        if (totalProcessed < this.items.size()) {
            return;
        }

        if (!this.failedProducts.isEmpty()) {
            log.warn("[Saga] One or more products failed to reserve. Triggering compensation...");
            releaseAllReservedProducts();
            cancelOrder("One or more products failed to reserve");
        } else {
            log.info("[Saga] All products reserved. Triggering payment...");
            InitiatePaymentCommand initiatePaymentCommand = InitiatePaymentCommand.builder()
                    .paymentId(UUID.randomUUID())
                    .orderId(this.orderId)
                    .customerId(this.customerId)
                    .totalAmount(calculateTotalAmount())
                    .build();
            commandGateway.send(initiatePaymentCommand);
        }
    }

    private BigDecimal calculateTotalAmount() {
        return this.items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ProductReservationReleasedEvent event) {
        log.info("[Saga] Received ProductReservationReleasedEvent for productId {}", event.getProductId());
        this.releasedProducts.add(event.getProductId());
        if (this.releasedProducts.containsAll(this.reservedProducts.keySet())) {
            log.info("[Saga] All product reservations released. Sending CancelOrderCommand.");
            cancelOrder("One or more product reservations failed");
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentInitiatedEvent event) {
        log.info("[Saga] Received PaymentInitiatedEvent for paymentId {}", event.getPaymentId());
        this.paymentId = event.getPaymentId();
        this.paymentStatus = event.getPaymentStatus();
        this.updatedAt = event.getUpdatedAt();

        UpdatePaymentStatusCommand updatePaymentStatusCommand = UpdatePaymentStatusCommand.builder()
                .orderId(orderId)
                .paymentId(paymentId)
                .paymentStatus(paymentStatus)
                .message("Payment initiated")
                .updatedAt(updatedAt)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .build();
        commandGateway.send(updatePaymentStatusCommand);

    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void on(OrderCancelledEvent event) {
        log.info("[Saga] Received OrderCancelledEvent for orderId {}", event.getOrderId());
    }

    private void cancelOrder(String message) {
        if (!isOrderCancelled) {
            isOrderCancelled = true;
            CancelOrderCommand cancelOrderCommand = CancelOrderCommand.builder()
                    .orderId(orderId)
                    .customerId(customerId)
                    .message(message)
                    .build();
            commandGateway.send(cancelOrderCommand);
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentProcessedEvent event) {
        log.info("[Saga] Received PaymentProcessedEvent for paymentId {}", event.getPaymentId());
        this.paymentStatus = event.getPaymentStatus();
        this.totalAmount = event.getTotalAmount();
        this.updatedAt = event.getUpdatedAt();
        UpdatePaymentStatusCommand updatePaymentStatusCommand = UpdatePaymentStatusCommand.builder()
                .orderId(orderId)
                .paymentId(paymentId)
                .paymentStatus(paymentStatus)
                .message("Payment processed")
                .updatedAt(updatedAt)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .build();
        commandGateway.send(updatePaymentStatusCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentStatusUpdatedEvent event) {
        log.info("[Saga] Received PaymentStatusUpdatedEvent for paymentId {}, status: {}", event.getPaymentId(), event.getPaymentStatus());
        this.paymentStatus = event.getPaymentStatus();
        this.updatedAt = event.getUpdatedAt();

        if (event.getPaymentStatus() == PaymentStatus.COMPLETED) {
            InitiateShippingCommand initiateShippingCommand = InitiateShippingCommand.builder()
                    .shippingId(UUID.randomUUID())
                    .orderId(orderId)
                    .customerId(customerId)
                    .address(address)
                    .city(city)
                    .state(state)
                    .zipCode(zipCode)
                    .customerName(customerName)
                    .customerEmail(customerEmail)
                    .build();
            commandGateway.send(initiateShippingCommand);
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShippingInitiatedEvent event) {
        log.info("[Saga] Received ShippingInitiatedEvent for shippingId {}", event.getShippingId());
        this.shippingId = event.getShippingId();
        this.shippingStatus = event.getShippingStatus();
        this.updatedAt = event.getUpdatedAt();

        UpdateShippingStatusCommand updateShippingStatusCommand = UpdateShippingStatusCommand.builder()
                .orderId(orderId)
                .shippingId(shippingId)
                .shippingStatus(shippingStatus)
                .message("Shipping initiated")
                .updatedAt(updatedAt)
                .build();

        commandGateway.send(updateShippingStatusCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShippingProcessedEvent event) {
        log.info("[Saga] Received ShippingProcessedEvent for shippingId {}", event.getShippingId());
        this.shippingStatus = event.getShippingStatus();
        this.updatedAt = event.getUpdatedAt();

        UpdateShippingStatusCommand updateShippingStatusCommand = UpdateShippingStatusCommand.builder()
                .orderId(orderId)
                .shippingId(shippingId)
                .shippingStatus(shippingStatus)
                .message("Shipping processed")
                .updatedAt(updatedAt)
                .build();

        commandGateway.send(updateShippingStatusCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShippingStatusUpdatedEvent event) {
        log.info("[Saga] Received ShippingStatusUpdatedEvent for shippingId {} with status: {}",
                event.getShippingId(),
                event.getShippingStatus()
        );
        this.shippingStatus = event.getShippingStatus();
        this.updatedAt = event.getUpdatedAt();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShippingDeliveredEvent event) {
        log.info("[Saga] Received ShippingDeliveredEvent for shippingId {}", event.getShippingId());
        CompleteOrderCommand completeOrderCommand = CompleteOrderCommand.builder()
                .orderId(orderId)
                .orderStatus(OrderStatus.COMPLETED)
                .shippingStatus(ShippingStatus.DELIVERED)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();
        commandGateway.send(completeOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCompletedEvent event) {
        log.info("[Saga] Received OrderCompletedEvent for orderId {}", event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentFailedEvent event) {
        log.info("[Saga] Received PaymentFailedEvent for paymentId {}", event.getPaymentId());
        releaseAllReservedProducts();
        cancelOrder("Payment failed");
    }

    private void releaseAllReservedProducts() {
        reservedProducts.forEach((productId, quantity) -> {
            log.info("[Saga] Releasing product reservation for productId {}", productId);
            ReleaseProductReservationCommand releaseProductReservationCommand = ReleaseProductReservationCommand.builder()
                    .orderId(orderId)
                    .productId(productId)
                    .customerId(customerId)
                    .quantity(quantity)
                    .build();
            commandGateway.send(releaseProductReservationCommand, new CommandCallback<ReleaseProductReservationCommand, Object>() {
                @Override
                public void onResult(CommandMessage<? extends ReleaseProductReservationCommand> commandMessage,
                                     CommandResultMessage<? extends Object> commandResultMessage) {
                    if (commandResultMessage.isExceptional()) {
                        log.error("[Saga] Failed to release reservation for productId {}: {}",
                                commandMessage.getPayload().getProductId(),
                                commandResultMessage.exceptionResult().getMessage());
                    } else {
                        log.info("[Saga] Successfully released reservation for productId {}",
                                commandMessage.getPayload().getProductId());
                    }
                }
            });
        });
    }

}
