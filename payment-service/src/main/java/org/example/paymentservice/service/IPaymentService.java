package org.example.paymentservice.service;

import org.example.common.constants.PaymentStatus;
import org.example.paymentservice.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IPaymentService {
    void createPayment(Payment payment);
    Payment getPaymentById(UUID paymentId);
    List<Payment> getPaymentsByOrderId(UUID orderId);
    List<Payment> getAllPayments();
    void cancelPayment(UUID paymentId, PaymentStatus status, String reason, LocalDateTime cancelledAt);
    void updateStatus(UUID paymentId, PaymentStatus status, String reason, LocalDateTime updatedAt);
    void updateStatus(UUID paymentId, UUID orderId, UUID customerId, BigDecimal totalAmount, PaymentStatus status, String message, LocalDateTime updatedAt);
}
