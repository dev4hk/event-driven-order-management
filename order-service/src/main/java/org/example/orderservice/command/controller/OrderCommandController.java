package org.example.orderservice.command.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.example.common.dto.CommonResponseDto;
import org.example.orderservice.command.InitiateOrderCommand;
import org.example.orderservice.command.RequestOrderCancellationCommand;
import org.example.orderservice.dto.CancelOrderDto;
import org.example.orderservice.dto.CreateOrderDto;
import org.springframework.http.MediaType;
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

    @PostMapping("/create")
    public CompletableFuture<CommonResponseDto<String>> create(@Valid @RequestBody CreateOrderDto dto) {
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

        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Order created", orderId.toString()));
    }

    @PutMapping("/cancel")
    public CompletableFuture<CommonResponseDto<String>> cancel(@Valid @RequestBody CancelOrderDto dto) {

        RequestOrderCancellationCommand command = RequestOrderCancellationCommand.builder()
                .orderId(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .message(dto.getMessage())
                .build();

        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Order canceled", dto.getOrderId().toString()));
    }
}
