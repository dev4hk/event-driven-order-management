package org.example.productservice.service;

import org.example.productservice.entity.Product;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    void createProduct(Product product);
    boolean updateProduct(Product product);
    void deleteProduct(UUID productId);
    List<Product> getAllProducts();
    Product getProductById(UUID productId);
}
