package org.example.paymentservice.event;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.example.common.events.PaymentFailedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventHandler {

    @EventHandler
    public void on(PaymentFailedEvent event) {
        log.info("[EventHandler] Received PaymentFailedEvent for orderId {} because {}", event.getOrderId(), event.getMessage());
    }

}
