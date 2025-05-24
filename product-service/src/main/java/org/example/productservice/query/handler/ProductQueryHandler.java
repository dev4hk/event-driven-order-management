package org.example.productservice.query.handler;

import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.example.productservice.dto.ProductResponseDto;
import org.example.productservice.query.GetAllProductsQuery;
import org.example.productservice.query.GetProductByIdQuery;
import org.example.productservice.service.IProductService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductQueryHandler {

    private final IProductService productService;

    @QueryHandler
    public List<ProductResponseDto> getAllProducts(GetAllProductsQuery query) {
        return productService.getAllProducts()
                .stream()
                .map(ProductResponseDto::new)
                .toList();
    }

    @QueryHandler
    public ProductResponseDto getProductById(GetProductByIdQuery query) {
        return new ProductResponseDto(productService.getProductById(query.getProductId()));
    }
}
