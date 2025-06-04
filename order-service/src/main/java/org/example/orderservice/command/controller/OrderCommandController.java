package org.example.orderservice.command.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.example.common.dto.CommonResponseDto;
import org.example.orderservice.command.CreateOrderCommand;
import org.example.orderservice.command.RequestOrderCancellationCommand;
import org.example.orderservice.dto.CancelOrderDto;
import org.example.orderservice.dto.CreateOrderDto;
import org.example.orderservice.dto.OrderResponseDto;
import org.example.orderservice.query.GetOrderByIdQuery;
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
    private final QueryGateway queryGateway;

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

    @PutMapping("/cancel")
    public CompletableFuture<CommonResponseDto<String>> cancel(@Valid @RequestBody CancelOrderDto dto) {
        UUID orderId = dto.getOrderId();
        OrderResponseDto orderData = queryGateway.query(new GetOrderByIdQuery(orderId), ResponseTypes.instanceOf(OrderResponseDto.class)).join();
        if(!orderData.getOrderId().equals(orderId) || !orderData.getCustomerId().equals(dto.getCustomerId())) {
            return CompletableFuture.completedFuture(CommonResponseDto.failure("Cannot find order"));
        }
        RequestOrderCancellationCommand command = RequestOrderCancellationCommand.builder()
                .orderId(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .paymentId(orderData.getPaymentId())
                .shippingId(orderData.getShippingId())
                .items(orderData.getItems())
                .totalAmount(orderData.getTotalAmount())
                .reason(dto.getReason())
                .build();

        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Order canceled", dto.getOrderId().toString()));
    }
}
