package org.example.productservice.query.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.example.common.dto.CommonResponseDto;
import org.example.productservice.dto.ProductResponseDto;
import org.example.productservice.query.GetAllProductsQuery;
import org.example.productservice.query.GetProductByIdQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductQueryController {

    private final QueryGateway queryGateway;

    @GetMapping
    public CompletableFuture<CommonResponseDto<List<ProductResponseDto>>> getAll() {
        return queryGateway.query(
                new GetAllProductsQuery(),
                ResponseTypes.multipleInstancesOf(ProductResponseDto.class)
        ).thenApply(CommonResponseDto::success);
    }

    @GetMapping("/{productId}")
    public CompletableFuture<CommonResponseDto<ProductResponseDto>> getById(@PathVariable UUID productId) {
        return queryGateway.query(
                new GetProductByIdQuery(productId),
                ResponseTypes.instanceOf(ProductResponseDto.class)
        ).thenApply(CommonResponseDto::success);
    }
}
