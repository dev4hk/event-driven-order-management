package org.example.shippingservice.repository;

import org.example.common.constants.ShippingStatus;
import org.example.shippingservice.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShippingRepository extends JpaRepository<Shipping, UUID> {
    List<Shipping> findByStatus(ShippingStatus status);
    Optional<Shipping> findByOrderId(UUID orderId);
    boolean existsByOrderId(UUID orderId);
}
