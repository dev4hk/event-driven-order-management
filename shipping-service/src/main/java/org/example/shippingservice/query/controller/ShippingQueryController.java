package org.example.shippingservice.query.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.example.common.dto.CommonResponseDto;
import org.example.shippingservice.dto.ShippingResponseDto;
import org.example.shippingservice.query.GetAllShippingsQuery;
import org.example.common.query.GetShippingByIdQuery;
import org.example.shippingservice.query.GetShippingByOrderIdQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingQueryController {

    private final QueryGateway queryGateway;

    @GetMapping("/{shippingId}")
    public CompletableFuture<CommonResponseDto<ShippingResponseDto>> getShippingById(@PathVariable UUID shippingId) {
        return queryGateway.query(
                new GetShippingByIdQuery(shippingId),
                ResponseTypes.instanceOf(ShippingResponseDto.class)
        ).thenApply(CommonResponseDto::success);
    }

    @GetMapping("/order/{orderId}")
    public CompletableFuture<CommonResponseDto<ShippingResponseDto>> getShippingByOrderId(@PathVariable UUID orderId) {
        return queryGateway.query(
                new GetShippingByOrderIdQuery(orderId),
                ResponseTypes.instanceOf(ShippingResponseDto.class)
        ).thenApply(CommonResponseDto::success);
    }

    @GetMapping
    public CompletableFuture<CommonResponseDto<List<ShippingResponseDto>>> getAllShippings() {
        return queryGateway.query(
                new GetAllShippingsQuery(),
                ResponseTypes.multipleInstancesOf(ShippingResponseDto.class)
        ).thenApply(CommonResponseDto::success);
    }
}
