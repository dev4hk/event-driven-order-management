package org.example.shippingservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.example.common.events.ShippingCreatedEvent;
import org.example.common.events.ShippingProcessedEvent;
import org.example.shippingservice.entity.Shipping;
import org.example.shippingservice.service.IShippingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@ProcessingGroup("shipping-group")
public class ShippingProjection {

    private final IShippingService shippingService;

    @EventHandler
    public void on(ShippingCreatedEvent event) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(event, shipping);
        shipping.setUpdatedAt(LocalDateTime.now());
        shippingService.createShipping(shipping);
    }

    @EventHandler
    public void on(ShippingProcessedEvent event) {
        shippingService.updateShippingStatus(event.getShippingId(), event.getNewStatus(), event.getUpdatedAt());
    }
}
