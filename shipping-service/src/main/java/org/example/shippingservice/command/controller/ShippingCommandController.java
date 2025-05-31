package org.example.shippingservice.command.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.CommonResponseDto;
import org.example.shippingservice.command.ProcessShippingCommand;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingCommandController {

    private final CommandGateway commandGateway;

    @PutMapping("/ship/{shippingId}")
    public CompletableFuture<CommonResponseDto<String>> ship(@PathVariable("shippingId") UUID shippingId) {
        ProcessShippingCommand command = ProcessShippingCommand.builder()
                .shippingId(shippingId)
                .newStatus(ShippingStatus.SHIPPED)
                .build();
        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Shipping processed", shippingId.toString()));
    }

}
