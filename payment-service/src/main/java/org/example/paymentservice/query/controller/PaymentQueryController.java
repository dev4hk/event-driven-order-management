package org.example.paymentservice.query.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.example.common.dto.CommonResponseDto;
import org.example.paymentservice.dto.PaymentResponseDto;
import org.example.paymentservice.query.GetAllPaymentsQuery;
import org.example.paymentservice.query.GetPaymentByIdQuery;
import org.example.paymentservice.query.GetPaymentsByOrderIdQuery;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentQueryController {

    private final QueryGateway queryGateway;

    @GetMapping("/{id}")
    public CompletableFuture<CommonResponseDto<PaymentResponseDto>> getPaymentById(@PathVariable("paymentId") UUID paymentId) {
        return queryGateway.query(new GetPaymentByIdQuery(paymentId),
                        ResponseTypes.instanceOf(PaymentResponseDto.class))
                .thenApply(CommonResponseDto::success);
    }

    @GetMapping
    public CompletableFuture<CommonResponseDto<List<PaymentResponseDto>>> getAllPayments() {
        return queryGateway.query(new GetAllPaymentsQuery(),
                        ResponseTypes.multipleInstancesOf(PaymentResponseDto.class))
                .thenApply(CommonResponseDto::success);
    }

    @GetMapping("/order/{orderId}")
    public CompletableFuture<CommonResponseDto<List<PaymentResponseDto>>> getPaymentsByOrderId(@PathVariable("orderId") UUID orderId) {
        return queryGateway.query(new GetPaymentsByOrderIdQuery(orderId),
                        ResponseTypes.multipleInstancesOf(PaymentResponseDto.class))
                .thenApply(CommonResponseDto::success);
    }

}
