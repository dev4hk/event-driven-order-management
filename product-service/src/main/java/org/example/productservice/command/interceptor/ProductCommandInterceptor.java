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
import org.example.productservice.repository.ProductRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class ProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final ProductRepository productRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {
            if(command.getPayloadType().equals(CreateProductCommand.class)) {
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                Optional<Product> product = productRepository.findByProductIdAndActive(createProductCommand.getProductId(), true);
                if(product.isPresent()) {
                    throw new ResourceAlreadyExistsException("Product with id " + createProductCommand.getProductId() + " already exists");
                }
            } else if(command.getPayloadType().equals(UpdateProductCommand.class)) {
                UpdateProductCommand updateProductCommand = (UpdateProductCommand) command.getPayload();
                Optional<Product> product = productRepository.findByProductIdAndActive(updateProductCommand.getProductId(), true);
                if(product.isEmpty()) {
                    throw new ResourceNotFoundException("Product with id " + updateProductCommand.getProductId() + " not found");
                }
            } else if(command.getPayloadType().equals(DeleteProductCommand.class)) {
                DeleteProductCommand deleteProductCommand = (DeleteProductCommand) command.getPayload();
                Optional<Product> product = productRepository.findByProductIdAndActive(deleteProductCommand.getProductId(), true);
                if(product.isEmpty()) {
                    throw new ResourceNotFoundException("Product with id " + deleteProductCommand.getProductId() + " not found");
                }
            }
            return command;
        };
    }
}
