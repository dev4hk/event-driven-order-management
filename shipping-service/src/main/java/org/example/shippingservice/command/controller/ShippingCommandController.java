package org.example.shippingservice.command.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.example.common.commands.InitiateShippingCommand;
import org.example.common.dto.CommonResponseDto;
import org.example.shippingservice.command.DeliverShippingCommand;
import org.example.shippingservice.dto.ProcessShippingDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
@Validated
public class ShippingCommandController {

    private final CommandGateway commandGateway;

    @PutMapping("/ship")
    public CompletableFuture<CommonResponseDto<String>> ship(@Valid @RequestBody ProcessShippingDto dto) {
        UUID shippingId = UUID.randomUUID();
        InitiateShippingCommand command = InitiateShippingCommand.builder()
                .shippingId(shippingId)
                .orderId(dto.getOrderId())
                .shippingDetails(dto.getShippingDetails())
                .build();
        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Shipping processed", shippingId.toString()));
    }

    @PutMapping("/deliver/{shippingId}")
    public CompletableFuture<CommonResponseDto<String>> deliver(@PathVariable("shippingId") UUID shippingId) {
        DeliverShippingCommand command = DeliverShippingCommand.builder()
                .shippingId(shippingId)
                .build();
        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Shipping delivered", shippingId.toString()));
    }

}
