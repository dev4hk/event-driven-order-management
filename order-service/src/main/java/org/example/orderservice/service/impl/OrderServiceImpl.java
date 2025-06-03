package org.example.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.common.constants.OrderStatus;
import org.example.common.exception.ResourceNotFoundException;
import org.example.orderservice.entity.Order;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.IOrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));
    }

    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));
        existingOrder.setStatus(status);
        orderRepository.save(existingOrder);
    }

    @Override
    public void cancelOrder(UUID orderId, OrderStatus status, String reason) {
        Order existingOrder = getOrderById(orderId);
        existingOrder.setStatus(status);
        existingOrder.setReason(reason);
        orderRepository.save(existingOrder);
    }
}
