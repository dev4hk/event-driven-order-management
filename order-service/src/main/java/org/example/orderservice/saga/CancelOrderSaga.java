package org.example.orderservice.saga;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.example.common.commands.CancelPaymentCommand;
import org.example.common.commands.CancelShippingCommand;
import org.example.common.commands.ReleaseProductReservationCommand;
import org.example.common.commands.ValidateCustomerCommand;
import org.example.common.constants.PaymentStatus;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.OrderItemDto;
import org.example.common.events.*;
import org.example.orderservice.command.CancelOrderCommand;
import org.example.orderservice.command.CompleteOrderCancellationCommand;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Saga
@Slf4j
public class CancelOrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private UUID orderId;
    private UUID customerId;
    private UUID paymentId;
    private UUID shippingId;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
    private String message;
    private PaymentStatus paymentStatus;
    private ShippingStatus shippingStatus;

    private Set<UUID> releasedProducts = new HashSet<>();
    private LocalDateTime cancelledAt;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCancellationRequestedEvent event) {
        log.info("[Saga] Received OrderCancellationRequestedEvent for orderId {}", event.getOrderId());
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.paymentId = event.getPaymentId();
        this.shippingId = event.getShippingId();
        this.items = event.getItems();
        this.totalAmount = event.getTotalAmount();
        this.message = event.getMessage();

        ValidateCustomerCommand validateCustomerCommand = ValidateCustomerCommand.builder()
                .customerId(event.getCustomerId())
                .orderId(event.getOrderId())
                .build();
        commandGateway.send(validateCustomerCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(CustomerValidatedEvent event) {
        log.info("[Saga] Received CustomerValidatedEvent for customerId {}", event.getCustomerId());
        CancelOrderCommand cancelOrderCommand = CancelOrderCommand.builder()
                .orderId(orderId)
                .customerId(customerId)
                .message(message)
                .build();
        commandGateway.send(cancelOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(CustomerValidationFailedEvent event) {
        log.warn("[Saga] Received CustomerValidationFailedEvent for customerId {}", event.getCustomerId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCancelledEvent event) {
        log.info("[Saga] Received OrderCancelledEvent for orderId {}", event.getOrderId());
        this.items.forEach((orderItemDto) -> {
            log.info("[Saga] Releasing product reservation for productId {}", orderItemDto.getProductId());
            ReleaseProductReservationCommand releaseProductReservationCommand = ReleaseProductReservationCommand.builder()
                    .orderId(orderId)
                    .productId(orderItemDto.getProductId())
                    .customerId(customerId)
                    .quantity(orderItemDto.getQuantity())
                    .build();
            commandGateway.send(releaseProductReservationCommand);
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationReleasedEvent event) {
        log.info("[Saga] Received ProductReservationReleasedEvent for productId {}", event.getProductId());
        releasedProducts.add(event.getProductId());

        if (releasedProducts.size() == items.size()) {
            log.info("[Saga] All product reservations released for orderId {}", orderId);
            this.items = new ArrayList<>();
            CancelPaymentCommand cancelPaymentCommand = CancelPaymentCommand.builder()
                    .paymentId(paymentId)
                    .customerId(customerId)
                    .orderId(orderId)
                    .amount(totalAmount)
                    .message(message)
                    .build();
            commandGateway.send(cancelPaymentCommand);
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentCancelledEvent event) {
        log.info("[Saga] Received PaymentCancelledEvent for paymentId {}", event.getPaymentId());
        this.paymentStatus = PaymentStatus.CANCELLED;
        CancelShippingCommand cancelShippingCommand = CancelShippingCommand.builder()
                .shippingId(shippingId)
                .orderId(orderId)
                .message(message)
                .build();
        commandGateway.send(cancelShippingCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ShippingCancelledEvent event) {
        log.info("[Saga] Received ShippingCancelledEvent for shippingId {}", event.getShippingId());
        this.shippingStatus = ShippingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        CompleteOrderCancellationCommand completeOrderCancelledEvent = CompleteOrderCancellationCommand.builder()
                .orderId(orderId)
                .paymentStatus(paymentStatus)
                .shippingStatus(shippingStatus)
                .items(items)
                .message(message)
                .cancelledAt(cancelledAt)
                .build();
        commandGateway.send(completeOrderCancelledEvent);

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCancellationCompletedEvent event) {
        log.info("[Saga] Received OrderCancellationCompletedEvent for orderId {}", event.getOrderId());
    }
}
