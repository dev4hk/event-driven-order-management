package org.example.productservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.events.ProductCreatedEvent;
import org.example.common.events.ProductUpdatedEvent;
import org.example.common.events.ProductDeletedEvent;
import org.example.productservice.command.CreateProductCommand;
import org.example.productservice.command.UpdateProductCommand;
import org.example.productservice.command.DeleteProductCommand;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
public class ProductAggregate {

    @AggregateIdentifier
    private UUID productId;
    private String name;
    private BigDecimal price;
    private String description;
    private int stock;
    private boolean active;

    @CommandHandler
    public ProductAggregate(CreateProductCommand command) {
        ProductCreatedEvent event = new ProductCreatedEvent();
        BeanUtils.copyProperties(command, event);
        event.setActive(true);
        apply(event);
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.productId = event.getProductId();
        this.name = event.getName();
        this.price = event.getPrice();
        this.description = event.getDescription();
        this.stock = event.getStock();
        this.active = true;
    }

    @CommandHandler
    public void handle(UpdateProductCommand command) {
        ProductUpdatedEvent event = new ProductUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        apply(event);
    }

    @EventSourcingHandler
    public void on(ProductUpdatedEvent event) {
        this.name = event.getName();
        this.price = event.getPrice();
        this.description = event.getDescription();
        this.stock = event.getStock();
    }

    @CommandHandler
    public void handle(DeleteProductCommand command) {
        ProductDeletedEvent event = new ProductDeletedEvent();
        BeanUtils.copyProperties(command, event);
        apply(event);
    }

    @EventSourcingHandler
    public void on(ProductDeletedEvent event) {
        this.active = false;
    }
}
