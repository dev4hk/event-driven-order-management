package org.example.shippingservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.example.common.events.*;
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
    public void on(ShippingProcessedEvent event) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(event, shipping);
        shipping.setAddress(event.getShippingDetails().getAddress());
        shipping.setCity(event.getShippingDetails().getCity());
        shipping.setState(event.getShippingDetails().getState());
        shipping.setZipCode(event.getShippingDetails().getZipCode());
        shipping.setName(event.getShippingDetails().getName());
        shippingService.processShipping(shipping);
    }

    @EventHandler
    public void on(ShippingDeliveredEvent event) {
        shippingService.updateShippingStatus(
                event.getShippingId(),
                event.getOrderId(),
                event.getShippingStatus(),
                event.getMessage(),
                event.getUpdatedAt()
        );
    }

    @EventHandler
    public void on(ShippingFailedEvent event) {
        shippingService.updateShippingStatus(
                event.getShippingId(),
                event.getOrderId(),
                event.getShippingStatus(),
                event.getMessage(),
                event.getUpdatedAt()
        );
    }
}
