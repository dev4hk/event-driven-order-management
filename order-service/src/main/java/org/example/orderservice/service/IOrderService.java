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

    void cancelOrder(UUID orderId, OrderStatus status, String message, LocalDateTime cancelledAt);

    void updateOrderStatus(
            UUID orderId,
            PaymentStatus paymentStatus,
            ShippingStatus shippingStatus,
            List<OrderItemDto> items,
            String message,
            LocalDateTime cancelledAt
    );

    void updatePaymentStatus(UUID orderId, UUID paymentId, PaymentStatus paymentStatus, String message, LocalDateTime updatedAt);

    void updateShippingStatus(UUID orderId, UUID shippingId, ShippingStatus status, String message, LocalDateTime updatedAt);

    void updateCustomerInfo(UUID orderId, UUID customerId, String customerName, String customerEmail, String message, LocalDateTime updatedAt);

    void updateOrder(UUID orderId, OrderStatus orderStatus, String message);
}
