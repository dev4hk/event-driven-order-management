package org.example.orderservice.saga;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.example.common.commands.*;
import org.example.common.constants.OrderStatus;
import org.example.common.constants.PaymentStatus;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.OrderItemDto;
import org.example.common.dto.ShippingDetails;
import org.example.common.events.*;
import org.example.orderservice.command.CancelOrderCommand;
import org.example.orderservice.command.UpdateCustomerInfoCommand;
import org.example.orderservice.command.UpdateShippingStatusCommand;
import org.springframework.beans.factory.annotation.Autowired;

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
    private String message;
    private ShippingDetails shippingDetails;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderInitiatedEvent event) {
        log.info("[Saga] Received OrderInitiatedEvent for orderId {}, validating customer {}", event.getOrderId(), event.getCustomerId());

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

        commandGateway.send(
                command,
                (commandMessage, commandResultMessage) -> {
                    if (commandResultMessage.isExceptional()) {
                        Throwable exception = commandResultMessage.exceptionResult();
                        log.error("[Saga] Failed to dispatch ValidateCustomerCommand for customer {}: {}", customerId, exception.getMessage());
                        message = "Customer validation dispatch failed: " + exception.getMessage();
                        cancelOrder(message);
                    }
                }
        );
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(CustomerValidatedEvent event) {
        log.info("[Saga] Received CustomerValidatedEvent for customerId {}. Reserving {} products", event.getCustomerId(), items.size());
        this.customerName = event.getCustomerName();
        this.customerEmail = event.getCustomerEmail();

        UpdateCustomerInfoCommand updateCustomerInfoCommand = UpdateCustomerInfoCommand.builder()
                .orderId(orderId)
                .customerId(customerId)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .build();

        commandGateway.send(
                updateCustomerInfoCommand,
                (commandMessage, commandResultMessage) -> {
                    if (commandResultMessage.isExceptional()) {
                        Throwable exception = commandResultMessage.exceptionResult();
                        log.error("[Saga] Failed to dispatch UpdateCustomerInfoCommand for customer {}: {}", customerId, exception.getMessage());
                        message = "Customer info update dispatch failed: " + exception.getMessage();
                        cancelOrder(message);
                    }
                }
        );
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(CustomerInfoUpdatedEvent event) {
        log.info("[Saga] Received CustomerInfoUpdatedEvent for customerId {}", event.getCustomerId());
        this.message = event.getMessage();
        this.updatedAt = event.getUpdatedAt();

        for (OrderItemDto item : this.items) {
            ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                    .orderId(orderId)
                    .productId(item.getProductId())
                    .customerId(customerId)
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build();
            commandGateway.send(
                    reserveProductCommand,
                    (commandMessage, commandResultMessage) -> {
                        if (commandResultMessage.isExceptional()) {
                            Throwable exception = commandResultMessage.exceptionResult();
                            log.error("[Saga] Failed to dispatch ReserveProductCommand for product {}: {}", item.getProductId(), exception.getMessage());
                            message = "Product reservation dispatch failed: " + exception.getMessage();
                            failedProducts.add(item.getProductId());
                            checkReservationCompletion();
                        }
                    }
            );
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ProductReservedEvent event) {
        log.info("[Saga] Received ProductReservedEvent for productId {}", event.getProductId());
        this.reservedProducts.put(event.getProductId(), event.getQuantity());
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
        } else {
            log.info("[Saga] All products reserved.");
            this.totalAmount = calculateTotalAmount();
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
        if (!event.getCustomerId().equals(this.customerId)) {
            FailPaymentCommand failPaymentCommand = FailPaymentCommand.builder()
                    .orderId(orderId)
                    .message("Payment is mismatching customer ID for this order")
                    .build();
            commandGateway.send(failPaymentCommand);
        } else if (!event.getTotalAmount().equals(this.totalAmount)) {
            FailPaymentCommand failPaymentCommand = FailPaymentCommand.builder()
                    .orderId(orderId)
                    .message("Payment is mismatching total amount for this order")
                    .build();
            commandGateway.send(failPaymentCommand);
        } else {
            this.paymentId = event.getPaymentId();
            ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                    .paymentId(event.getPaymentId())
                    .orderId(event.getOrderId())
                    .customerId(event.getCustomerId())
                    .totalAmount(event.getTotalAmount())
                    .build();
            commandGateway.send(processPaymentCommand, (commandMessage, commandResultMessage) -> {
                if (commandResultMessage.isExceptional()) {
                    Throwable exception = commandResultMessage.exceptionResult();
                    log.error("[Saga] Failed to dispatch ProcessPaymentCommand for paymentId {}: {}", event.getPaymentId(), exception.getMessage());
                    message = "Payment processing dispatch failed: " + exception.getMessage();
                    FailPaymentCommand failPaymentCommand = FailPaymentCommand.builder()
                            .orderId(orderId)
                            .message(message)
                            .build();
                    commandGateway.send(failPaymentCommand);
                }
            });
        }
    }

    private void cancelPayment(String message) {
        CancelPaymentCommand cancelPaymentCommand = CancelPaymentCommand.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .customerId(customerId)
                .amount(totalAmount)
                .message(message)
                .build();
        commandGateway.send(cancelPaymentCommand);
    }

    private void on(PaymentCancelledEvent event) {
        log.info("[Saga] Received PaymentCancelledEvent for paymentId {}", event.getPaymentId());
        this.paymentStatus = event.getPaymentStatus();
        this.updatedAt = event.getCancelledAt();
        releaseAllReservedProducts();
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
    @EndSaga
    public void on(OrderCancelledEvent event) {
        log.info("[Saga] Received OrderCancelledEvent for orderId {}", event.getOrderId());
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
                .build();
        commandGateway.send(
                updatePaymentStatusCommand,
                (commandMessage, commandResultMessage) -> {
                    if (commandResultMessage.isExceptional()) {
                        Throwable exception = commandResultMessage.exceptionResult();
                        log.error("[Saga] Failed to dispatch UpdatePaymentStatusCommand: {}", exception.getMessage());
                        message = "Payment status update dispatch failed: " + exception.getMessage();
                        cancelPayment(message);
                    }
                }
        );
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentStatusUpdatedEvent event) {
        log.info("[Saga] Received PaymentStatusUpdatedEvent for paymentId {}, status: {}", event.getPaymentId(), event.getPaymentStatus());
        this.paymentStatus = event.getPaymentStatus();
        this.updatedAt = event.getUpdatedAt();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShippingInitiatedEvent event) {
        log.info("[Saga] Received ShippingInitiatedEvent for shippingId {}", event.getShippingId());
        if (this.paymentStatus != PaymentStatus.COMPLETED) {
            FailShippingCommand failShippingCommand = FailShippingCommand.builder()
                    .orderId(event.getOrderId())
                    .shippingId(event.getShippingId())
                    .message("Payment is not completed")
                    .build();
            commandGateway.send(failShippingCommand);
        }
        else {
            this.shippingId = event.getShippingId();
            this.shippingDetails = event.getShippingDetails();

            ProcessShippingCommand processShippingCommand = ProcessShippingCommand.builder()
                    .shippingId(event.getShippingId())
                    .orderId(event.getOrderId())
                    .shippingDetails(event.getShippingDetails())
                    .build();
            commandGateway.send(
                    processShippingCommand,
                    (commandMessage, commandResultMessage) -> {
                        if (commandResultMessage.isExceptional()) {
                            Throwable exception = commandResultMessage.exceptionResult();
                            log.error("[Saga] Failed to dispatch ProcessShippingCommand: {}", exception.getMessage());
                            message = "Shipping dispatch failed: " + exception.getMessage();
                            FailShippingCommand failShippingCommand = FailShippingCommand.builder()
                                    .orderId(event.getOrderId())
                                    .shippingId(event.getShippingId())
                                    .message(message)
                                    .build();
                            commandGateway.send(failShippingCommand);
                        }
                    }
            );
        }
    }


    private void cancelShipping() {
        CancelShippingCommand cancelShippingCommand = CancelShippingCommand.builder()
                .shippingId(shippingId)
                .orderId(orderId)
                .message(message)
                .build();
        commandGateway.send(cancelShippingCommand);
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

        commandGateway.send(
                updateShippingStatusCommand,
                (commandMessage, commandResultMessage) -> {
                    if (commandResultMessage.isExceptional()) {
                        Throwable exception = commandResultMessage.exceptionResult();
                        log.error("[Saga] Failed to dispatch UpdateShippingStatusCommand: {}", exception.getMessage());
                        message = "Shipping status update dispatch failed: " + exception.getMessage();
                        cancelShipping();
                    }
                }
        );
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShippingStatusUpdatedEvent event) {
        log.info("[Saga] Received ShippingStatusUpdatedEvent for shippingId {} with status: {}",
                event.getShippingId(),
                event.getShippingStatus()
        );
        this.shippingStatus = event.getShippingStatus();
        this.updatedAt = event.getUpdatedAt();

        if(event.getShippingStatus() == ShippingStatus.DELIVERED) {
            log.info("[Saga] Shipping delivered for shippingId {}, order is now completed", event.getShippingId());
            this.orderStatus = OrderStatus.COMPLETED;
            SagaLifecycle.end();
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShippingDeliveredEvent event) {
        log.info("[Saga] Received ShippingDeliveredEvent for shippingId {}", event.getShippingId());

        UpdateShippingStatusCommand updateShippingStatusCommand = UpdateShippingStatusCommand.builder()
                .orderId(orderId)
                .shippingId(shippingId)
                .shippingStatus(event.getShippingStatus())
                .message("Shipping delivered, order completed")
                .updatedAt(updatedAt)
                .build();

        commandGateway.send(
                updateShippingStatusCommand,
                (commandMessage, commandResultMessage) -> {
                    if (commandResultMessage.isExceptional()) {
                        Throwable exception = commandResultMessage.exceptionResult();
                        log.error("[Saga] Failed to dispatch CompleteOrderCommand: {}", exception.getMessage());
                        message = "Order completion dispatch failed: " + exception.getMessage();
                        cancelShipping();
                    }
                }
        );
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

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCancellationRequestedEvent event) {
        log.info("[Saga] Cancellation has been requested for orderId {}. The CreateOrderSaga will now terminate.", event.getOrderId());
    }

}
