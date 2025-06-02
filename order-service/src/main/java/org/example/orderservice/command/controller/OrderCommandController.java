package org.example.orderservice.command.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.example.common.dto.CommonResponseDto;
import org.example.orderservice.command.CancelOrderCommand;
import org.example.orderservice.command.CreateOrderCommand;
import org.example.orderservice.command.UpdateOrderCommand;
import org.example.orderservice.dto.CreateOrderDto;
import org.example.orderservice.dto.UpdateOrderDto;
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

        CreateOrderCommand command = CreateOrderCommand.builder()
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

    @PutMapping("/update")
    public CompletableFuture<CommonResponseDto<String>> update(@Valid @RequestBody UpdateOrderDto dto) {
        UpdateOrderCommand command = UpdateOrderCommand.builder()
                .orderId(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .items(dto.getItems())
                .totalAmount(dto.getTotalAmount())
                .build();

        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Order updated", dto.getOrderId().toString()));
    }

    @DeleteMapping("/delete/{orderId}")
    public CompletableFuture<CommonResponseDto<String>> cancel(@PathVariable("orderId") UUID orderId) {
        CancelOrderCommand command = CancelOrderCommand.builder()
                .orderId(orderId)
                .build();

        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Order canceled", orderId.toString()));
    }
}
