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
    public void on(PaymentProcessedEvent event) {
        Payment payment = new Payment();
        BeanUtils.copyProperties(event, payment);
        paymentService.createPayment(payment);
    }

    @EventHandler
    public void on(PaymentCancelledEvent event) {
        paymentService.cancelPayment(
                event.getPaymentId(),
                event.getPaymentStatus(),
                event.getMessage(),
                event.getCancelledAt()
        );
    }

    @EventHandler
    public void on(PaymentStatusRolledBackEvent event) {
        paymentService.rollBackPayment(
                event.getPaymentId(),
                event.getOrderId(),
                event.getPaymentStatus()
        );
    }

}
