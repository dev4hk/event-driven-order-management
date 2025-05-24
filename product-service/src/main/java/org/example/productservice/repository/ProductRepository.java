package org.example.productservice.repository;

import org.example.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByProductIdAndActive(UUID productId, boolean active);
    List<Product> findByActive(boolean active);
}
