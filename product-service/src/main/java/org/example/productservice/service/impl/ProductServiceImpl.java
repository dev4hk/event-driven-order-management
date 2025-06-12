package org.example.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.productservice.entity.Product;
import org.example.productservice.exception.InvalidProductDataException;
import org.example.productservice.repository.ProductRepository;
import org.example.productservice.service.IProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
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

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findByActive(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(UUID productId) {
        return productRepository.findByProductIdAndActive(productId, true)
                .orElseThrow(() -> new RuntimeException("Product with id " + productId + " not found"));
    }

    @Override
    public void reserveProduct(UUID productId, int quantity, boolean isActive) {
        Product product = getProductById(productId);
        if (product.getStock() < quantity) {
            throw new InvalidProductDataException("Not enough stock for product " + productId);
        }
        product.setStock(product.getStock() - quantity);
        product.setActive(isActive);
        productRepository.save(product);
    }

    @Override
    public void releaseProductReservation(UUID productId, int quantity, boolean isActive) {
        Product product = getProductById(productId);
        product.setStock(product.getStock() + quantity);
        product.setActive(isActive);
        productRepository.save(product);
    }
}
