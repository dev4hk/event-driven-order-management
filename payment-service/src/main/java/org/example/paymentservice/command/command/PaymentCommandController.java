package org.example.paymentservice.command.command;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.example.common.commands.InitiatePaymentCommand;
import org.example.common.commands.ProcessPaymentCommand;
import org.example.common.dto.CommonResponseDto;
import org.example.paymentservice.dto.ProcessPaymentDto;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "/api/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class PaymentCommandController {

    private final CommandGateway commandGateway;

    @PostMapping("/process")
    public CompletableFuture<CommonResponseDto<String>> process(@Valid @RequestBody ProcessPaymentDto dto) {
        UUID paymentId = UUID.randomUUID();
        InitiatePaymentCommand command = InitiatePaymentCommand.builder()
                .paymentId(paymentId)
                .orderId(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .totalAmount(dto.getAmount())
                .build();
        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Payment processed", paymentId.toString()));
    }
}
