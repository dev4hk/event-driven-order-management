package org.example.paymentservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.common.constants.PaymentStatus;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.exception.InvalidPaymentDataException;
import org.example.paymentservice.repository.PaymentRepository;
import org.example.paymentservice.service.IPaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public void createPayment(Payment payment) {
        if (paymentRepository.existsById(payment.getPaymentId())) {
            throw new ResourceAlreadyExistsException("Payment with this ID already exists: " + payment.getPaymentId());
        }
        paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with ID " + paymentId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
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

}
