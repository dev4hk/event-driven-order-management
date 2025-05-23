package org.example.productservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.example.common.events.ProductCreatedEvent;
import org.example.common.events.ProductDeletedEvent;
import org.example.common.events.ProductUpdatedEvent;
import org.example.productservice.entity.Product;
import org.example.productservice.service.IProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("product-group")
public class ProductProjection {

    private final IProductService iProductService;

    @EventHandler
    public void on(ProductCreatedEvent event) {
        Product product = new Product();
        BeanUtils.copyProperties(event, product);
        iProductService.createProduct(product);
    }

    @EventHandler
    public void on(ProductUpdatedEvent event) {
        Product product = new Product();
        BeanUtils.copyProperties(event, product);
        iProductService.updateProduct(product);
    }

    @EventHandler
    public void on(ProductDeletedEvent event) {
        iProductService.deleteProduct(event.getProductId());
    }
}

