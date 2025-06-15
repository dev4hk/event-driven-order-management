package org.example.paymentservice.query.handler;

import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.query.GetAllPaymentsQuery;
import org.example.common.query.GetPaymentByIdQuery;
import org.example.paymentservice.query.GetPaymentsByOrderIdQuery;
import org.example.paymentservice.service.IPaymentService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentQueryHandler {

    private final IPaymentService paymentService;

    @QueryHandler
    public Payment handle(GetPaymentByIdQuery query) {
        return paymentService.getPaymentById(query.getPaymentId());
    }

    @QueryHandler
    public List<Payment> handle(GetPaymentsByOrderIdQuery query) {
        return paymentService.getPaymentsByOrderId(query.getOrderId());
    }

    @QueryHandler
    public List<Payment> handle(GetAllPaymentsQuery query) {
        return paymentService.getAllPayments();
    }
}