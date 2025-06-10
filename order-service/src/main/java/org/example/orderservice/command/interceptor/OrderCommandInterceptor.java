package org.example.orderservice.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.example.common.commands.UpdatePaymentStatusCommand;
import org.example.common.constants.OrderStatus;
import org.example.common.constants.ShippingStatus;
import org.example.common.dto.OrderItemDto;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.orderservice.command.*;
import org.example.orderservice.entity.Order;
import org.example.orderservice.exception.InvalidOrderDataException;
import org.example.orderservice.exception.OrderLifecycleViolationException;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class OrderCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final OrderRepository orderRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {
            Object payload = command.getPayload();

            if (payload instanceof InitiateOrderCommand) {
                validateCreateOrder((InitiateOrderCommand) payload);
            } else if (payload instanceof CancelOrderCommand) {
                validateCancelOrder((CancelOrderCommand) payload);
            } else if (payload instanceof CompleteOrderCommand) {
                validateCompleteOrder((CompleteOrderCommand) payload);
            } else if (payload instanceof RequestOrderCancellationCommand) {
                validateRequestOrderCancellation((RequestOrderCancellationCommand) payload);
            } else if (payload instanceof UpdatePaymentStatusCommand) {
                validateUpdatePaymentStatus((UpdatePaymentStatusCommand) payload);
            } else if (payload instanceof UpdateShippingStatusCommand) {
                validateUpdateShippingStatus((UpdateShippingStatusCommand) payload);
            }

            return command;
        };
    }

    private Order getExistingOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " does not exist."));
    }

    private void validateCreateOrder(InitiateOrderCommand command) {
        if (command.getOrderId() == null) {
            throw new InvalidOrderDataException("Order ID must not be null.");
        }
        if (orderRepository.existsById(command.getOrderId())) {
            throw new ResourceAlreadyExistsException("Order with ID " + command.getOrderId() + " already exists.");
        }
        validateOrderItems(command.getItems());
        validateTotalAmount(command.getTotalAmount());
    }

    private void validateCancelOrder(CancelOrderCommand command) {

        if (command.getOrderId() == null || command.getCustomerId() == null) {
            throw new InvalidOrderDataException("Order ID and Customer ID must not be null.");
        }

        Order existingOrder = getExistingOrder(command.getOrderId());

        if (existingOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderLifecycleViolationException("Order with ID " + command.getOrderId() + " is already cancelled.");
        }

        if (existingOrder.getStatus() == OrderStatus.COMPLETED) {
            ShippingStatus shippingStatus = existingOrder.getShippingStatus();
            if (shippingStatus.equals(ShippingStatus.SHIPPED) || shippingStatus.equals(ShippingStatus.DELIVERED) || shippingStatus.equals(ShippingStatus.CANCELLED)) {
                throw new OrderLifecycleViolationException("Cannot cancel a completed or delivered or cancelled order with ID: " + command.getOrderId());
            }
        }
    }

    private void validateCompleteOrder(CompleteOrderCommand command) {

        if (
                command.getOrderId() == null
                        || command.getOrderStatus() == null
                        || command.getPaymentStatus() == null
                        || command.getShippingStatus() == null

        ) {
            throw new InvalidOrderDataException("Order ID, Customer ID, Payment ID, Shipping ID, Order Status, Payment Status, Shipping Status, Customer Name and Customer Email must not be null.");
        }

        Order existingOrder = getExistingOrder(command.getOrderId());

        if (existingOrder.getStatus().equals(OrderStatus.COMPLETED) || existingOrder.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new OrderLifecycleViolationException("Order with ID " + command.getOrderId() + " is already completed or cancelled.");
        }

    }

    private void validateRequestOrderCancellation(RequestOrderCancellationCommand command) {
        if (command.getOrderId() == null || command.getCustomerId() == null) {
            throw new InvalidOrderDataException("Order ID, Customer ID must not be null.");
        }
        Order existingOrder = getExistingOrder(command.getOrderId());

        if (existingOrder.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new OrderLifecycleViolationException("Order with ID " + command.getOrderId() + " is already cancelled.");
        }

        command.setPaymentId(existingOrder.getPaymentId());
        command.setShippingId(existingOrder.getShippingId());
        command.setTotalAmount(existingOrder.getTotalAmount());

        List<OrderItemDto> items = OrderMapper.toDtoList(existingOrder.getItems());
        command.setItems(items);
    }

    private void validateUpdatePaymentStatus(UpdatePaymentStatusCommand command) {
        if (command.getOrderId() == null || command.getPaymentStatus() == null || command.getUpdatedAt() == null) {
            throw new InvalidOrderDataException("Order ID, Payment Status, and Updated At must not be null.");
        }

        Order existingOrder = getExistingOrder(command.getOrderId());

        if (existingOrder.getStatus().equals(OrderStatus.COMPLETED) || existingOrder.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new OrderLifecycleViolationException("Order with ID " + command.getOrderId() + " is already completed or cancelled.");
        }
    }


    private void validateUpdateShippingStatus(UpdateShippingStatusCommand command) {
        if(command.getOrderId() == null || command.getShippingStatus() == null || command.getUpdatedAt() == null) {
            throw new InvalidOrderDataException("Order ID, Shipping Status, and Updated At must not be null.");
        }
    }

    private void validateOrderItems(List<OrderItemDto> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderDataException("Order must contain at least one item.");
        }
        for (OrderItemDto item : items) {
            if (item == null) {
                throw new InvalidOrderDataException("Order item cannot be null.");
            }
            if (item.getQuantity() <= 0 || item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidOrderDataException("Each order item must have a quantity greater than 0 and a valid price. Problem with item: " + item.getProductId());
            }
        }
    }

    private void validateTotalAmount(BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderDataException("Total amount must be greater than zero.");
        }
    }
}