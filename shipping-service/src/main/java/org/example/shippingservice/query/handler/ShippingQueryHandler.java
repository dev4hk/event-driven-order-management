package org.example.shippingservice.query.handler;

import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.example.shippingservice.dto.ShippingResponseDto;
import org.example.shippingservice.entity.Shipping;
import org.example.shippingservice.query.GetAllShippingsQuery;
import org.example.shippingservice.query.GetShippingByIdQuery;
import org.example.shippingservice.query.GetShippingByOrderIdQuery;
import org.example.shippingservice.service.IShippingService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShippingQueryHandler {

    private final IShippingService shippingService;

    @QueryHandler
    public ShippingResponseDto handle(GetShippingByIdQuery query) {
        Shipping shipping = shippingService.getById(query.getShippingId());
        return mapToDto(shipping);
    }

    @QueryHandler
    public ShippingResponseDto handle(GetShippingByOrderIdQuery query) {
        Shipping shipping = shippingService.getByOrderId(query.getOrderId());
        return mapToDto(shipping);
    }

    @QueryHandler
    public List<ShippingResponseDto> handle(GetAllShippingsQuery query) {
        return shippingService.getAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ShippingResponseDto mapToDto(Shipping shipping) {
        return ShippingResponseDto.builder()
                .shippingId(shipping.getShippingId())
                .orderId(shipping.getOrderId())
                .status(shipping.getStatus())
                .shippedAt(shipping.getShippedAt())
                .deliveredAt(shipping.getDeliveredAt())
                .updatedAt(shipping.getUpdatedAt())
                .build();
    }
}
