package org.example.orderservice.query.handler;

import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.example.orderservice.dto.OrderResponseDto;
import org.example.orderservice.entity.Order;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.query.GetAllOrdersQuery;
import org.example.orderservice.query.GetOrderByIdQuery;
import org.example.orderservice.service.IOrderService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderQueryHandler {

    private final IOrderService iOrderService;

    @QueryHandler
    public List<OrderResponseDto> handle(GetAllOrdersQuery query) {
        List<Order> orders = iOrderService.getAllOrders();
        return orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @QueryHandler
    public OrderResponseDto handle(GetOrderByIdQuery query) {
        Order order = iOrderService.getOrderById(query.getOrderId());
        return OrderMapper.toDto(order);
    }
}
