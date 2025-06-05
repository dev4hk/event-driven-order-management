package org.example.orderservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.example.common.events.*;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderItem;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.service.IOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public void on(OrderCancelledEvent event) {
        iOrderService.cancelOrder(event.getOrderId(), event.getStatus(), event.getReason(), event.getCancelledAt());
    }

    @EventHandler
    public void on(OrderCompletedEvent event) {
        List<OrderItem> items = OrderMapper.toEntityList(event.getItems());
        iOrderService.updateOrderStatus(
                event.getOrderId(),
                event.getCustomerId(),
                event.getPaymentId(),
                event.getShippingId(),
                event.getTotalAmount(),
                event.getOrderStatus(),
                event.getPaymentStatus(),
                event.getShippingStatus(),
                event.getCompletedAt(),
                event.getCustomerName(),
                event.getCustomerEmail(),
                items
        );
    }

    @EventHandler
    public void on(OrderCancellationCompletedEvent event) {
        List<OrderItem> items = OrderMapper.toEntityList(event.getItems());
        iOrderService.updateOrderStatus(
                event.getOrderId(),
                event.getPaymentStatus(),
                event.getShippingStatus(),
                event.getItems(),
                event.getReason(),
                event.getCancelledAt()
        );
    }

}
