package org.example.productservice.command.aggregate;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.common.commands.ReleaseProductReservationCommand;
import org.example.common.commands.ReserveProductCommand;
import org.example.common.events.*;
import org.example.productservice.command.CreateProductCommand;
import org.example.productservice.command.DeleteProductCommand;
import org.example.productservice.command.UpdateProductCommand;
import org.example.productservice.events.ProductCreatedEvent;
import org.example.productservice.events.ProductDeletedEvent;
import org.example.productservice.events.ProductUpdatedEvent;
import org.example.productservice.exception.InvalidProductDataException;
import org.example.productservice.exception.InvalidProductStateException;
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

    @CommandHandler
    public void handle(ReserveProductCommand command) {
        if (!this.active) {
            throw new InvalidProductStateException("Product is inactive.");
        }
        else if (this.stock < command.getQuantity()) {
            throw new InvalidProductDataException("Not enough stock for product " + command.getProductId());
        }
        else if (command.getPrice() == null || this.price.compareTo(command.getPrice()) != 0) {
            throw new InvalidProductDataException("Product price mismatch for product " + command.getProductId());
        }
        else {
            ProductReservedEvent event = ProductReservedEvent.builder()
                    .orderId(command.getOrderId())
                    .productId(command.getProductId())
                    .customerId(command.getCustomerId())
                    .quantity(command.getQuantity())
                    .build();
            event.setActive(command.getQuantity() != this.stock);
            apply(event);
        }
    }

    @EventSourcingHandler
    public void on(ProductReservedEvent event) {
        this.stock -= event.getQuantity();
        this.active = event.isActive();
    }

    @CommandHandler
    public void handle(ReleaseProductReservationCommand command) {
        ProductReservationReleasedEvent event = ProductReservationReleasedEvent.builder()
                .orderId(command.getOrderId())
                .productId(command.getProductId())
                .customerId(command.getCustomerId())
                .quantity(command.getQuantity())
                .build();
        if (this.stock + command.getQuantity() > 0) {
            event.setActive(true);
        }
        apply(event);
    }

    @EventSourcingHandler
    public void on(ProductReservationReleasedEvent event) {
        this.stock += event.getQuantity();
        this.active = event.isActive();
    }
}
