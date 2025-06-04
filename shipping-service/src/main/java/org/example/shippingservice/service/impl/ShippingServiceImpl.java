package org.example.shippingservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.common.constants.ShippingStatus;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.shippingservice.entity.Shipping;
import org.example.shippingservice.repository.ShippingRepository;
import org.example.shippingservice.service.IShippingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShippingServiceImpl implements IShippingService {

    private final ShippingRepository shippingRepository;

    @Override
    public Shipping getById(UUID shippingId) {
        return shippingRepository.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found with ID: " + shippingId));
    }

    @Override
    public Shipping getByOrderId(UUID orderId) {
        return shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found for order id: " + orderId));
    }

    @Override
    public List<Shipping> getAll() {
        return shippingRepository.findAll();
    }

    @Override
    public void createShipping(Shipping shipping) {
        Optional<Shipping> existingShipping = shippingRepository.findByOrderId(shipping.getOrderId());
        if (existingShipping.isPresent()) {
            throw new ResourceAlreadyExistsException("Shipping already exists for order ID: " + shipping.getOrderId());
        }
        shippingRepository.save(shipping);
    }

    @Override
    public void updateShippingStatus(UUID shippingId, ShippingStatus newStatus, LocalDateTime updatedAt) {
        Shipping existingShipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found with ID: " + shippingId));
        existingShipping.setStatus(newStatus);
        existingShipping.setUpdatedAt(updatedAt);

        shippingRepository.save(existingShipping);
    }

    @Override
    public void cancelShipping(UUID shippingId, String reason, ShippingStatus status, LocalDateTime cancelledAt) {
        Shipping existingShipping = getById(shippingId);
        existingShipping.setStatus(status);
        existingShipping.setUpdatedAt(cancelledAt);
        existingShipping.setReason(reason);
        shippingRepository.save(existingShipping);
    }
}