package org.example.shippingservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.example.common.events.ShippingCancelledEvent;
import org.example.common.events.ShippingInitiatedEvent;
import org.example.common.events.ShippingDeliveredEvent;
import org.example.common.events.ShippingProcessedEvent;
import org.example.shippingservice.entity.Shipping;
import org.example.shippingservice.service.IShippingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("shipping-group")
public class ShippingProjection {

    private final IShippingService shippingService;

    @EventHandler
    public void on(ShippingInitiatedEvent event) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(event, shipping);
        shippingService.createShipping(shipping);
    }

    @EventHandler
    public void on(ShippingProcessedEvent event) {
        shippingService.updateShippingStatus(event.getShippingId(), event.getShippingStatus(), event.getUpdatedAt());
    }

    @EventHandler
    public void on(ShippingDeliveredEvent event) {
        shippingService.updateShippingStatus(event.getShippingId(), event.getShippingStatus(), event.getUpdatedAt());
    }

    @EventHandler
    public void on(ShippingCancelledEvent event) {
        shippingService.cancelShipping(event.getShippingId(), event.getMessage(), event.getShippingStatus(), event.getCancelledAt());
    }



}
