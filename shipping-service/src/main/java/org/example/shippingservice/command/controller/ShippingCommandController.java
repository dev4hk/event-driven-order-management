package org.example.shippingservice.command.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.example.common.commands.InitiateShippingCommand;
import org.example.common.dto.CommonResponseDto;
import org.example.shippingservice.command.DeliverShippingCommand;
import org.example.shippingservice.dto.ProcessShippingDto;
import org.example.common.query.GetShippingByIdQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
@Validated
public class ShippingCommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PutMapping("/ship")
    public ResponseEntity<CommonResponseDto<?>> ship(@Valid @RequestBody ProcessShippingDto dto) {
        UUID shippingId = UUID.randomUUID();
        InitiateShippingCommand command = InitiateShippingCommand.builder()
                .shippingId(shippingId)
                .orderId(dto.getOrderId())
                .shippingDetails(dto.getShippingDetails())
                .build();

        try (SubscriptionQueryResult<CommonResponseDto, CommonResponseDto> queryResult = queryGateway.subscriptionQuery(
                new GetShippingByIdQuery(shippingId),
                ResponseTypes.instanceOf(CommonResponseDto.class),
                ResponseTypes.instanceOf(CommonResponseDto.class)
        )) {
            commandGateway.send(
                    command,
                    (commandMessage, commandResult) -> {
                        if (commandResult.isExceptional()) {
                            ResponseEntity
                                    .internalServerError()
                                    .body(CommonResponseDto.failure("Shipping failed: " + commandResult.exceptionResult().getMessage()));
                        }
                    }
            );
            return ResponseEntity.ok(queryResult.updates().blockFirst());
        }
    }

    @PutMapping("/deliver/{shippingId}")
    public ResponseEntity<CommonResponseDto<?>> deliver(@PathVariable("shippingId") UUID shippingId) {
        DeliverShippingCommand command = DeliverShippingCommand.builder()
                .shippingId(shippingId)
                .build();

        try (SubscriptionQueryResult<CommonResponseDto, CommonResponseDto> queryResult = queryGateway.subscriptionQuery(
                new GetShippingByIdQuery(shippingId),
                ResponseTypes.instanceOf(CommonResponseDto.class),
                ResponseTypes.instanceOf(CommonResponseDto.class)
        )) {
            commandGateway.send(
                    command,
                    (commandMessage, commandResult) -> {
                        if (commandResult.isExceptional()) {
                            ResponseEntity
                                    .internalServerError()
                                    .body(CommonResponseDto.failure("Delivery failed: " + commandResult.exceptionResult().getMessage()));
                        }
                    }
            );
            return ResponseEntity.ok(queryResult.updates().blockFirst());
        }
    }

}
