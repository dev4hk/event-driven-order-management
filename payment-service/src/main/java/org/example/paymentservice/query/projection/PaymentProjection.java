package org.example.paymentservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.example.common.events.*;
import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.service.IPaymentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@ProcessingGroup("payment-group")
public class PaymentProjection {

    private final IPaymentService paymentService;

    @EventHandler
    public void on(PaymentInitiatedEvent event) {
        Payment payment = new Payment();
        BeanUtils.copyProperties(event, payment);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentService.createPayment(payment);
    }

    @EventHandler
    public void on(PaymentProcessedEvent event) {
        paymentService.updateStatus(
                event.getPaymentId(),
                event.getOrderId(),
                event.getCustomerId(),
                event.getTotalAmount(),
                event.getStatus(),
                event.getMessage(),
                event.getUpdatedAt()
        );
    }

    @EventHandler
    public void on(PaymentFailedEvent event) {
        Payment payment = new Payment();
        BeanUtils.copyProperties(event, payment);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentService.createPayment(payment);
    }

    @EventHandler
    public void on(PaymentCancelledEvent event) {
        paymentService.cancelPayment(
                event.getPaymentId(),
                event.getStatus(),
                event.getMessage(),
                event.getCancelledAt()
        );
    }

}
