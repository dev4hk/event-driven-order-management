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
    public void on(OrderInitiatedEvent event) {
        Order order = new Order();
        BeanUtils.copyProperties(event, order);
        List<OrderItem> items = OrderMapper.toEntityList(event.getItems());
        order.setItems(items);
        iOrderService.createOrder(order);
    }

    @EventHandler
    public void on(PaymentStatusUpdatedEvent event) {
        iOrderService.updatePaymentStatus(
                event.getOrderId(),
                event.getPaymentId(),
                event.getPaymentStatus(),
                event.getMessage(),
                event.getUpdatedAt(),
                event.getCustomerName(),
                event.getCustomerEmail()
        );
    }

    @EventHandler
    public void on(ShippingStatusUpdatedEvent event) {
        iOrderService.updateShippingStatus(
                event.getOrderId(),
                event.getShippingId(),
                event.getShippingStatus(),
                event.getMessage(),
                event.getUpdatedAt()
        );
    }

    @EventHandler
    public void on(OrderCompletedEvent event) {
        iOrderService.updateOrderStatus(
                event.getOrderId(),
                event.getOrderStatus(),
                event.getMessage(),
                event.getCompletedAt()
        );
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        iOrderService.cancelOrder(event.getOrderId(), event.getOrderStatus(), event.getMessage(), event.getCancelledAt());
    }

    @EventHandler
    public void on(OrderCancellationCompletedEvent event) {
        List<OrderItem> items = OrderMapper.toEntityList(event.getItems());
        iOrderService.updateOrderStatus(
                event.getOrderId(),
                event.getPaymentStatus(),
                event.getShippingStatus(),
                event.getItems(),
                event.getMessage(),
                event.getCancelledAt()
        );
    }

    @EventHandler
    public void on(CustomerInfoUpdatedEvent event) {
        iOrderService.updateCustomerInfo(
                event.getOrderId(),
                event.getCustomerId(),
                event.getCustomerName(),
                event.getCustomerEmail(),
                event.getMessage(),
                event.getUpdatedAt()
        );
    }

}
