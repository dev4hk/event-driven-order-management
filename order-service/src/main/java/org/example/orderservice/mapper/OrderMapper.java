package org.example.orderservice.mapper;

import org.example.common.dto.OrderItemDto;
import org.example.orderservice.dto.OrderResponseDto;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderItem;

import java.util.List;

public class OrderMapper {

    public static OrderResponseDto toDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setCustomerId(order.getCustomerId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderItemDto> items = order.getItems().stream().map(OrderMapper::toItemDto).toList();
        dto.setItems(items);

        return dto;
    }

    private static OrderItemDto toItemDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(item.getProductId());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        return dto;
    }
}

