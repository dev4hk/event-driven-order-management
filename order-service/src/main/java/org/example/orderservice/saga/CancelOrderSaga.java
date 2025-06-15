package org.example.orderservice.saga;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.example.common.commands.*;
import org.example.common.constants.PaymentStatus;
import org.example.common.dto.CommonResponseDto;
import org.example.common.dto.OrderItemDto;
import org.example.common.events.*;
import org.example.orderservice.command.CancelOrderCommand;
import org.example.orderservice.command.RollbackCancelOrderCommand;
import org.example.orderservice.command.UpdatePaymentStatusCommand;
import org.example.orderservice.events.PaymentStatusUpdatedEvent;
import org.example.orderservice.query.GetOrderByIdQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Saga
@Slf4j
public class CancelOrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private UUID orderId;
    private UUID customerId;
    private UUID paymentId;
    private UUID shippingId;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
    private String reasonForCancellation;
    private String errorMessage;

    private Set<UUID> releasedProducts = new HashSet<>();
    private LocalDateTime updatedAt;
    private AtomicBoolean rollbackInitiated = new AtomicBoolean(false);

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
        this.reasonForCancellation = event.getMessage();

        ValidateCustomerCommand validateCustomerCommand = ValidateCustomerCommand.builder()
                .customerId(event.getCustomerId())
                .orderId(event.getOrderId())
                .build();

        commandGateway.send(
                validateCustomerCommand,
                (commandMessage, commandResult) -> {
                    if (commandResult.isExceptional()) {
                        log.error("[Saga] Failed to validate customer for orderId {}: {}",
                                commandMessage.getPayload().getOrderId(),
                                commandResult.exceptionResult().getMessage());
                        queryUpdateEmitter.emit(
                                GetOrderByIdQuery.class,
                                query -> true,
                                CommonResponseDto.failure("Failed to validate customer: " + commandResult.exceptionResult().getMessage())
                        );
                        SagaLifecycle.end();
                    }
                }
        );
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(CustomerValidatedEvent event) {
        log.info("[Saga] Received CustomerValidatedEvent for customerId {}", event.getCustomerId());
        if (paymentId != null) {
            cancelPayment();
        } else {
            releaseAllProducts();
        }
    }

    private void cancelPayment() {
        CancelPaymentCommand cancelPaymentCommand = CancelPaymentCommand.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .customerId(customerId)
                .message(reasonForCancellation)
                .amount(totalAmount)
                .build();
        commandGateway.send(
                cancelPaymentCommand,
                (commandMessage, commandResult) -> {
                    if (commandResult.isExceptional()) {
                        log.error("[Saga] Failed to cancel payment for orderId {}: {}, terminating saga",
                                commandMessage.getPayload().getOrderId(),
                                commandResult.exceptionResult().getMessage());
                        queryUpdateEmitter.emit(
                                GetOrderByIdQuery.class,
                                query -> true,
                                CommonResponseDto.failure("Failed to cancel payment: " + commandResult.exceptionResult().getMessage())
                        );
                        SagaLifecycle.end();
                    }
                }
        );
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentCancelledEvent event) {
        log.info("[Saga] Received PaymentCancelledEvent for paymentId {}", event.getPaymentId());
        UpdatePaymentStatusCommand updatePaymentStatusCommand = UpdatePaymentStatusCommand.builder()
                .orderId(orderId)
                .paymentId(paymentId)
                .paymentStatus(PaymentStatus.CANCELLED)
                .message(reasonForCancellation)
                .build();
        commandGateway.send(
                updatePaymentStatusCommand,
                (commandMessage, commandResult) -> {
                    if (commandResult.isExceptional()) {
                        log.error("[Saga] Failed to update payment status for orderId {}: {}, rolling back payment",
                                commandMessage.getPayload().getOrderId(),
                                commandResult.exceptionResult().getMessage());
                        rollbackPayment();
                    }
                }
        );
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentStatusUpdatedEvent event) {
        log.info("[Saga] Received PaymentStatusUpdatedEvent for paymentId {}, status: {}", event.getPaymentId(), event.getPaymentStatus());
        releaseAllProducts();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationReleasedEvent event) {
        if (rollbackInitiated.get()) {
            return;
        }
        log.info("[Saga] Received ProductReservationReleasedEvent for productId {}", event.getProductId());
        releasedProducts.add(event.getProductId());

        if (releasedProducts.size() == items.size()) {
            log.info("[Saga] All product reservations released for orderId {}", orderId);
            cancelOrder();
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCancelledEvent event) {
        log.info("[Saga] Received OrderCancelledEvent for orderId {}", event.getOrderId());
        queryUpdateEmitter.emit(
                GetOrderByIdQuery.class,
                query -> true,
                CommonResponseDto.success("Order cancelled", event.getOrderId().toString())
        );
    }

    private void rollbackPayment() {
        RollBackPaymentStatusCommand rollbackPaymentStatusCommand = RollBackPaymentStatusCommand.builder()
                .orderId(orderId)
                .paymentId(paymentId)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();
        commandGateway.send(rollbackPaymentStatusCommand);
    }

    private void releaseAllProducts() {
        if (items == null || items.isEmpty()) {
            cancelOrder();
            return;
        }

        this.items.forEach((orderItemDto) -> {
            log.info("[Saga] Releasing product reservation for productId {}", orderItemDto.getProductId());
            ReleaseProductReservationCommand releaseProductReservationCommand = ReleaseProductReservationCommand.builder()
                    .orderId(orderId)
                    .productId(orderItemDto.getProductId())
                    .customerId(customerId)
                    .quantity(orderItemDto.getQuantity())
                    .build();
            commandGateway.send(
                    releaseProductReservationCommand,
                    (commandMessage, commandResult) -> {
                        if (commandResult.isExceptional() && rollbackInitiated.compareAndSet(false, true)) {
                            String errorMessage = String.format(
                                    "[Saga] Failed to release product reservation for orderId %s. Initiating order cancellation rollback. Reason: %s",
                                    commandMessage.getPayload().getOrderId(),
                                    commandResult.exceptionResult().getMessage()
                            );
                            log.error(errorMessage);
                            rollbackOrderCancellation(errorMessage);
                        }

                    }
            );
        });
    }

    private void cancelOrder() {
        CancelOrderCommand cancelOrderCommand = CancelOrderCommand.builder()
                .orderId(orderId)
                .customerId(customerId)
                .message(reasonForCancellation)
                .build();
        commandGateway.send(cancelOrderCommand);
    }


    private void rollbackOrderCancellation(String reason) {
        log.info("[Saga] Rolling back order cancellation for orderId {}", orderId);
        RollbackCancelOrderCommand command = RollbackCancelOrderCommand.builder()
                .orderId(orderId)
                .message(reason)
                .build();
        commandGateway.send(command);
        queryUpdateEmitter.emit(
                GetOrderByIdQuery.class,
                query -> true,
                CommonResponseDto.failure("Failed to cancel order, manual intervention required with reason: " + reason)
        );
        SagaLifecycle.end();
    }


}
