package org.example.orderservice.service;

import org.example.common.constants.OrderStatus;
import org.example.common.constants.PaymentStatus;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.OrderItemDto;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IOrderService {
    void createOrder(Order order);
    void deleteOrder(UUID orderId);
    List<Order> getAllOrders();
    Order getOrderById(UUID orderId);
    void updateOrderStatus(
            UUID orderId,
            UUID customerId,
            UUID paymentId,
            UUID shippingId,
            BigDecimal totalAmount,
            OrderStatus orderStatus,
            PaymentStatus paymentStatus,
            ShippingStatus shippingStatus,
            LocalDateTime updatedAt,
            String customerName,
            String customerEmail,
            List<OrderItem> items
    );
    void cancelOrder(UUID orderId, OrderStatus status, String reason, LocalDateTime cancelledAt);

    void updateOrderStatus(
            UUID orderId,
            PaymentStatus paymentStatus,
            ShippingStatus shippingStatus,
            List<OrderItemDto> items,
            String reason,
            LocalDateTime cancelledAt
    );

    void updateShippingStatus(UUID shippingId, ShippingStatus status, LocalDateTime updatedAt);
}
