package org.example.productservice.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.productservice.command.CreateProductCommand;
import org.example.productservice.command.DeleteProductCommand;
import org.example.productservice.command.UpdateProductCommand;
import org.example.productservice.entity.Product;
import org.example.productservice.exception.InvalidProductDataException;
import org.example.productservice.repository.ProductRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class ProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final ProductRepository productRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {
            Object payload = command.getPayload();

            if (payload instanceof CreateProductCommand) {
                validateCreateProduct((CreateProductCommand) payload);
            } else if (payload instanceof UpdateProductCommand) {
                validateUpdateProduct((UpdateProductCommand) payload);
            } else if (payload instanceof DeleteProductCommand) {
                validateDeleteProduct((DeleteProductCommand) payload);
            }

            return command;
        };
    }

    private Product getActiveProductById(UUID productId) {
        return productRepository.findByProductIdAndActive(productId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Active product with ID " + productId + " not found."));
    }

    private void validateCommonProductData(UUID productId, String name, String description, BigDecimal price, int stock) {
        if (productId == null || name == null || description == null || price == null) {
            throw new InvalidProductDataException("Product ID, Name, Description, and Price must not be null.");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductDataException("Price must be greater than zero.");
        }
        if (stock < 0) {
            throw new InvalidProductDataException("Stock cannot be negative.");
        }
    }

    private void validateCreateProduct(CreateProductCommand command) {
        validateCommonProductData(
                command.getProductId(),
                command.getName(),
                command.getDescription(),
                command.getPrice(),
                command.getStock()
        );

        if (productRepository.findByProductIdAndActive(command.getProductId(), true).isPresent()) {
            throw new ResourceAlreadyExistsException("Product with ID " + command.getProductId() + " already exists.");
        }
    }

    private void validateUpdateProduct(UpdateProductCommand command) {
        validateCommonProductData(
                command.getProductId(),
                command.getName(),
                command.getDescription(),
                command.getPrice(),
                command.getStock()
        );

        getActiveProductById(command.getProductId());

    }

    private void validateDeleteProduct(DeleteProductCommand command) {
        if (command.getProductId() == null) {
            throw new InvalidProductDataException("Product ID must not be null for deletion.");
        }

        getActiveProductById(command.getProductId());
    }
}