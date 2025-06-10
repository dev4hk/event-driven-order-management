package org.example.orderservice.mapper;

import org.example.common.dto.OrderItemDto;
import org.example.orderservice.dto.OrderResponseDto;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderItem;

import java.util.ArrayList; // Import ArrayList
import java.util.List;
import java.util.stream.Collectors; // Import Collectors

public class OrderMapper {

    public static OrderResponseDto toDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setCustomerId(order.getCustomerId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getOrderStatus().name());
        dto.setPaymentId(order.getPaymentId());
        dto.setShippingId(order.getShippingId());
        dto.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemDto> items = order.getItems().stream()
                .map(OrderMapper::toItemDto)
                .collect(Collectors.toCollection(ArrayList::new));

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

    public static OrderItem toEntity(OrderItemDto dto) {
        return OrderItem.builder()
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .build();
    }

    public static List<OrderItem> toEntityList(List<OrderItemDto> dtoList) {
        if (dtoList == null) {
            return new ArrayList<>();
        }
        return dtoList.stream()
                .map(OrderMapper::toEntity)
                .collect(Collectors.toList());
    }

    public static List<OrderItemDto> toDtoList(List<OrderItem> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(OrderMapper::toItemDto)
                .collect(Collectors.toList());
    }
}