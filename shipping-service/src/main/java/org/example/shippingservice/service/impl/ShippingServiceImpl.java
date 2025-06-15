package org.example.shippingservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.common.constants.ShippingStatus;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.shippingservice.entity.Shipping;
import org.example.shippingservice.repository.ShippingRepository;
import org.example.shippingservice.service.IShippingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShippingServiceImpl implements IShippingService {

    private final ShippingRepository shippingRepository;

    @Override
    @Transactional(readOnly = true)
    public Shipping getById(UUID shippingId) {
        return shippingRepository.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found with ID: " + shippingId));
    }

    @Override
    @Transactional(readOnly = true)
    public Shipping getByOrderId(UUID orderId) {
        return shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found for order id: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shipping> getAll() {
        return shippingRepository.findAll();
    }

    @Override
    public void processShipping(Shipping shipping) {
        Optional<Shipping> optionalShipping = shippingRepository.findByOrderId(shipping.getOrderId());
        if (optionalShipping.isPresent()) {
            throw new ResourceAlreadyExistsException("Shipping already exists for order ID: " + shipping.getOrderId());
        }
        shippingRepository.save(shipping);
    }

    @Override
    public void updateShippingStatus(UUID shippingId, UUID orderId, ShippingStatus newStatus, String message, LocalDateTime updatedAt) {
        Shipping existingShipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found with ID: " + shippingId));
        if(!existingShipping.getOrderId().equals(orderId)) {
            throw new ResourceNotFoundException("Shipping not found for order id: " + orderId);
        }
        existingShipping.setShippingStatus(newStatus);
        existingShipping.setMessage(message);
        existingShipping.setUpdatedAt(updatedAt);
        shippingRepository.save(existingShipping);
    }
}