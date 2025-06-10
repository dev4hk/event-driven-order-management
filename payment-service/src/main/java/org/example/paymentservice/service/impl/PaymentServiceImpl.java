package org.example.paymentservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.common.constants.PaymentStatus;
import org.example.common.exception.ResourceNotFoundException;
import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.exception.InvalidPaymentDataException;
import org.example.paymentservice.repository.PaymentRepository;
import org.example.paymentservice.service.IPaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public void createPayment(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with ID " + paymentId + " not found"));
    }

    @Override
    public List<Payment> getPaymentsByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public void cancelPayment(UUID paymentId, PaymentStatus status, String reason, LocalDateTime cancelledAt) {
        Payment payment = getPaymentById(paymentId);
        payment.setPaymentStatus(status);
        payment.setMessage(reason);
        payment.setUpdatedAt(cancelledAt);
        paymentRepository.save(payment);
    }

    @Override
    public void updateStatus(UUID paymentId, PaymentStatus status, String reason, LocalDateTime updatedAt) {
        Payment payment = getPaymentById(paymentId);
        payment.setPaymentStatus(status);
        payment.setMessage(reason);
        payment.setUpdatedAt(updatedAt);
        paymentRepository.save(payment);
    }

    @Override
    public void updateStatus(UUID paymentId, UUID orderId, UUID customerId, BigDecimal totalAmount, PaymentStatus status, String message, LocalDateTime updatedAt) {
        Payment payment = getPaymentById(paymentId);
        if(payment.getOrderId() != null && !payment.getOrderId().equals(orderId)) {
            throw new InvalidPaymentDataException("Order ID mismatch");
        }
        if(payment.getCustomerId() != null && !payment.getCustomerId().equals(customerId)) {
            throw new InvalidPaymentDataException("Customer ID mismatch");
        }
        payment.setTotalAmount(totalAmount);
        payment.setPaymentStatus(status);
        payment.setMessage(message);
        payment.setUpdatedAt(updatedAt);
        paymentRepository.save(payment);
    }

}
