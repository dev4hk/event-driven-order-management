package org.example.shippingservice.service;

import org.example.common.constants.ShippingStatus;
import org.example.shippingservice.entity.Shipping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IShippingService {
    Shipping getById(UUID shippingId);
    Shipping getByOrderId(UUID orderId);
    List<Shipping> getAll();
    void createShipping(Shipping shipping);
    void updateShippingStatus(UUID shippingId, ShippingStatus newStatus, LocalDateTime updatedAt);
    void cancelShipping(UUID shippingId, String reason, ShippingStatus status, LocalDateTime cancelledAt);
}