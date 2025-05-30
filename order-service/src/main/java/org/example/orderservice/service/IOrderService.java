package org.example.orderservice.service;

import org.example.common.constants.OrderStatus;
import org.example.orderservice.entity.Order;

import java.util.List;
import java.util.UUID;

public interface IOrderService {
    void createOrder(Order order);
    void updateOrder(Order order);
    void deleteOrder(UUID orderId);
    List<Order> getAllOrders();
    Order getOrderById(UUID orderId);
    void updateOrderStatus(UUID orderId, OrderStatus status);
    void cancelOrder(UUID orderId, OrderStatus status, String reason);
}
