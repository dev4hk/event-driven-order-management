package org.example.orderservice.query.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.example.common.dto.CommonResponseDto;
import org.example.orderservice.dto.OrderResponseDto;
import org.example.orderservice.query.GetAllOrdersQuery;
import org.example.orderservice.query.GetOrderByIdQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderQueryController {

    private final QueryGateway queryGateway;

    @GetMapping
    public CompletableFuture<ResponseEntity<CommonResponseDto<List<OrderResponseDto>>>> getAllOrders() {
        return queryGateway.query(new GetAllOrdersQuery(), ResponseTypes.multipleInstancesOf(OrderResponseDto.class))
                .thenApply(orders -> ResponseEntity.ok(CommonResponseDto.success("Fetched all orders", orders)));
    }

    @GetMapping("/{orderId}")
    public CompletableFuture<ResponseEntity<CommonResponseDto<OrderResponseDto>>> getOrderById(@PathVariable("orderId") UUID orderId) {
        return queryGateway.query(new GetOrderByIdQuery(orderId), ResponseTypes.instanceOf(OrderResponseDto.class))
                .thenApply(order -> ResponseEntity.ok(CommonResponseDto.success("Fetched order", order)));
    }
}

