package org.example.productservice.service;

import org.example.productservice.entity.Product;

import java.util.UUID;

public interface IProductService {
    void createProduct(Product product);
    boolean updateProduct(Product product);
    void deleteProduct(UUID productId);
}
