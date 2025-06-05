package org.example.orderservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.example.common.events.ShippingDataUpdatedEvent;
import org.example.orderservice.service.IOrderService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusUpdateProjection {

    private final IOrderService iOrderService;

    @EventHandler
    public void on(ShippingDataUpdatedEvent event) {
        iOrderService.updateShippingStatus(event.getShippingId(), event.getStatus(), event.getUpdatedAt());
    }

}
