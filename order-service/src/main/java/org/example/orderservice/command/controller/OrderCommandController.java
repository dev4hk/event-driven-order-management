package org.example.orderservice.command.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.example.common.dto.CommonResponseDto;
import org.example.orderservice.command.InitiateOrderCommand;
import org.example.orderservice.command.RequestOrderCancellationCommand;
import org.example.orderservice.dto.CancelOrderDto;
import org.example.orderservice.dto.CreateOrderDto;
import org.example.orderservice.query.GetOrderByIdQuery;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class OrderCommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping("/create")
    public ResponseEntity<CommonResponseDto<?>> create(@Valid @RequestBody CreateOrderDto dto) {
        UUID orderId = UUID.randomUUID();

        InitiateOrderCommand command = InitiateOrderCommand.builder()
                .orderId(orderId)
                .customerId(dto.getCustomerId())
                .items(dto.getItems())
                .totalAmount(dto.getTotalAmount())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .build();

        try (SubscriptionQueryResult<CommonResponseDto, CommonResponseDto> queryResult = queryGateway.subscriptionQuery(
                new GetOrderByIdQuery(orderId),
                ResponseTypes.instanceOf(CommonResponseDto.class),
                ResponseTypes.instanceOf(CommonResponseDto.class)
        )) {
            commandGateway.send(
                    command,
                    (commandMessage, commandResult) -> {
                        if (commandResult.isExceptional()) {
                            ResponseEntity
                                    .internalServerError()
                                    .body(CommonResponseDto.failure("Order initialization failed: " + commandResult.exceptionResult().getMessage()));
                        }
                    }
            );
            return ResponseEntity.ok(queryResult.updates().blockFirst());
        }

    }

    @PutMapping("/cancel")
    public ResponseEntity<CommonResponseDto<?>> cancel(@Valid @RequestBody CancelOrderDto dto) {

        RequestOrderCancellationCommand command = RequestOrderCancellationCommand.builder()
                .orderId(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .message(dto.getMessage())
                .build();

        try (SubscriptionQueryResult<CommonResponseDto, CommonResponseDto> queryResult = queryGateway.subscriptionQuery(
                new GetOrderByIdQuery(dto.getOrderId()),
                ResponseTypes.instanceOf(CommonResponseDto.class),
                ResponseTypes.instanceOf(CommonResponseDto.class)
        )) {
            commandGateway.send(
                    command,
                    (commandMessage, commandResult) -> {
                        if (commandResult.isExceptional()) {
                            ResponseEntity
                                    .internalServerError()
                                    .body(CommonResponseDto.failure("Order Cancellation failed: " + commandResult.exceptionResult().getMessage()));
                        }
                    }
            );
            return ResponseEntity.ok(queryResult.updates().blockFirst());
        }
    }
}
