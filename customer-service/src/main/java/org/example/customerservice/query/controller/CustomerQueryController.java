package org.example.customerservice.query.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.example.common.dto.CommonResponseDto;
import org.example.customerservice.dto.CustomerResponseDto;
import org.example.customerservice.query.GetAllCustomersQuery;
import org.example.customerservice.query.GetCustomerByIdQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerQueryController {

    private final QueryGateway queryGateway;

    @GetMapping
    public CompletableFuture<CommonResponseDto<List<CustomerResponseDto>>> getAll() {
        return queryGateway.query(
                new GetAllCustomersQuery(),
                ResponseTypes.multipleInstancesOf(CustomerResponseDto.class)
        ).thenApply(CommonResponseDto::success);
    }

    @GetMapping("/{customerId}")
    public CompletableFuture<CommonResponseDto<CustomerResponseDto>> getById(@PathVariable("customerId") UUID customerId) {
        return queryGateway.query(
                new GetCustomerByIdQuery(customerId),
                ResponseTypes.instanceOf(CustomerResponseDto.class)
        ).thenApply(CommonResponseDto::success);
    }
}
