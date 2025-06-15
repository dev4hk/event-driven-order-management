package org.example.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.common.constants.OrderStatus;
import org.example.common.constants.PaymentStatus;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.OrderItemDto;
import org.example.common.exception.ResourceNotFoundException;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderItem;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.IOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;

    @Override
    public void createOrder(Order order) {
        setOrderInOrderItems(order);
        orderRepository.save(order);
    }

    private void setOrderInOrderItems(Order order) {
        if (order.getItems() != null) {
            order.getItems().forEach(orderItem -> orderItem.setOrder(order));
        }
    }

    @Override
    public void deleteOrder(UUID orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));
    }

    @Override
    public void cancelOrder(UUID orderId, OrderStatus status, String reason, LocalDateTime cancelledAt) {
        Order existingOrder = getOrderById(orderId);
        existingOrder.setOrderStatus(status);
        existingOrder.setMessage(reason);
        existingOrder.setUpdatedAt(cancelledAt);
        orderRepository.save(existingOrder);
    }

    @Override
    public void updateOrderStatus(
            UUID orderId,
            PaymentStatus paymentStatus,
            ShippingStatus shippingStatus,
            List<OrderItemDto> items,
            String reason,
            LocalDateTime cancelledAt
    ) {
        Order existingOrder = getOrderById(orderId);
        if (existingOrder.getItems() == null) {
            existingOrder.setItems(new ArrayList<>());
        } else {
            existingOrder.getItems().clear();
        }
        existingOrder.setPaymentStatus(paymentStatus);
        existingOrder.setShippingStatus(shippingStatus);
        existingOrder.setMessage(reason);
        existingOrder.setUpdatedAt(cancelledAt);
        orderRepository.save(existingOrder);
    }

    @Override
    public void updateShippingStatus(UUID orderId, UUID shippingId, ShippingStatus status, String message, LocalDateTime updatedAt) {
        Order existingOrder = getOrderById(orderId);
        if(existingOrder.getShippingId() == null || status.equals(ShippingStatus.SHIPPED)) {
            existingOrder.setShippingId(shippingId);
        }
        else if(!existingOrder.getShippingId().equals(shippingId)) {
            throw new ResourceNotFoundException("Order with this shipping ID does not exist: " + shippingId);
        }
        existingOrder.setShippingStatus(status);
        if(status.equals(ShippingStatus.DELIVERED)) {
            existingOrder.setOrderStatus(OrderStatus.COMPLETED);
        }
        existingOrder.setUpdatedAt(updatedAt);
        existingOrder.setMessage(message);
        orderRepository.save(existingOrder);
    }

    @Override
    public void updateCustomerInfo(UUID orderId, UUID customerId, String customerName, String customerEmail, String message, LocalDateTime updatedAt) {
        Order existingOrder = getOrderById(orderId);
        if(existingOrder.getCustomerId() == null) {
            throw new ResourceNotFoundException("Order with this customer ID does not exist: " + customerId);
        }
        if(!existingOrder.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException("Order with this customer ID does not exist: " + customerId);
        }
        existingOrder.setCustomerName(customerName);
        existingOrder.setCustomerEmail(customerEmail);
        existingOrder.setMessage(message);
        existingOrder.setUpdatedAt(updatedAt);
        orderRepository.save(existingOrder);
    }

    @Override
    public void updateOrder(UUID orderId, OrderStatus orderStatus, String message) {
        Order existingOrder = getOrderById(orderId);
        existingOrder.setOrderStatus(orderStatus);
        existingOrder.setMessage(message);
        orderRepository.save(existingOrder);
    }

    @Override
    public void updatePaymentStatus(UUID orderId, UUID paymentId, PaymentStatus paymentStatus, String message, LocalDateTime updatedAt) {
        Order existingOrder = getOrderById(orderId);
        if (existingOrder.getPaymentId() == null) {
            existingOrder.setPaymentId(paymentId);
        }
        if(existingOrder.getPaymentId() != null && !existingOrder.getPaymentId().equals(paymentId)) {
            throw new ResourceNotFoundException("Order with this payment ID does not exist: " + paymentId);
        }
        existingOrder.setPaymentStatus(paymentStatus);
        existingOrder.setUpdatedAt(updatedAt);
        existingOrder.setMessage(message);

        orderRepository.save(existingOrder);
    }

}
