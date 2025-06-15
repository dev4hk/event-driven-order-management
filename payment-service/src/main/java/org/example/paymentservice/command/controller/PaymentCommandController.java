package org.example.paymentservice.command.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.example.paymentservice.command.InitiatePaymentCommand;
import org.example.common.dto.CommonResponseDto;
import org.example.common.query.GetPaymentByIdQuery;
import org.example.paymentservice.dto.ProcessPaymentDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/payments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class PaymentCommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping("/process")
    public ResponseEntity<CommonResponseDto<?>> process(@Valid @RequestBody ProcessPaymentDto dto) {
        UUID paymentId = UUID.randomUUID();
        InitiatePaymentCommand command = InitiatePaymentCommand.builder()
                .paymentId(paymentId)
                .orderId(dto.getOrderId())
                .customerId(dto.getCustomerId())
                .totalAmount(dto.getAmount())
                .build();
        try (SubscriptionQueryResult<CommonResponseDto, CommonResponseDto> queryResult = queryGateway.subscriptionQuery(
                new GetPaymentByIdQuery(paymentId),
                ResponseTypes.instanceOf(CommonResponseDto.class),
                ResponseTypes.instanceOf(CommonResponseDto.class)
        )) {
            commandGateway.send(
                    command,
                    (commandMessage, commandResult) -> {
                        if (commandResult.isExceptional()) {
                            ResponseEntity
                                    .internalServerError()
                                    .body(CommonResponseDto.failure("Payment failed: " + commandResult.exceptionResult().getMessage()));
                        }
                    }
            );
            return ResponseEntity.ok(queryResult.updates().blockFirst());
        }
    }
}
