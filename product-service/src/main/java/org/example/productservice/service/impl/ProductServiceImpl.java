package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.entity.Product;
import org.example.productservice.repository.ProductRepository;
import org.example.productservice.service.IProductService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    @Override
    public void createProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public boolean updateProduct(Product product) {
        Product existing = productRepository.findByProductIdAndActive(product.getProductId(), true)
                .orElseThrow(() -> new RuntimeException("Product with id " + product.getProductId() + " not found"));
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        productRepository.save(existing);
        return true;
    }

    @Override
    public void deleteProduct(UUID productId) {
        Product existing = productRepository.findByProductIdAndActive(productId, true)
                .orElseThrow(() -> new RuntimeException("Product with id " + productId + " not found"));
        existing.setActive(false);
        productRepository.save(existing);
    }
}
