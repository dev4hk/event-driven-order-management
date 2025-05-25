package org.example.orderservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.example.common.events.OrderCancelledEvent;
import org.example.common.events.OrderCreatedEvent;
import org.example.common.events.OrderUpdatedEvent;
import org.example.orderservice.entity.Order;
import org.example.orderservice.service.IOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("order-group")
public class OrderProjection {

    private final IOrderService iOrderService;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        Order order = new Order();
        BeanUtils.copyProperties(event, order);
        iOrderService.createOrder(order);
    }

    @EventHandler
    public void on(OrderUpdatedEvent event) {
        Order order = new Order();
        BeanUtils.copyProperties(event, order);
        iOrderService.updateOrder(order);
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        iOrderService.deleteOrder(event.getOrderId());
    }

}
