package org.example.shippingservice.event;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.example.common.events.ShippingFailedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShippingEventHandler {

    @EventHandler
    public void on(ShippingFailedEvent event) {
        log.info("[EventHandler] Received ShippingFailedEvent for orderId {} because {}", event.getOrderId(), event.getMessage());
    }
}
