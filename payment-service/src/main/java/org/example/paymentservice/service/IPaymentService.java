package org.example.paymentservice.service;

import org.example.paymentservice.entity.Payment;

import java.util.List;
import java.util.UUID;

public interface IPaymentService {
    void createPayment(Payment payment);
    Payment getPaymentById(UUID paymentId);
    List<Payment> getPaymentsByOrderId(UUID orderId);
    List<Payment> getAllPayments();
}

