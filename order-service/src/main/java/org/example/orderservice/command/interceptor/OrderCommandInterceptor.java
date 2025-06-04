package org.example.orderservice.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.example.common.constants.OrderStatus;
import org.example.common.dto.OrderItemDto;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.orderservice.command.CancelOrderCommand;
import org.example.orderservice.command.CompleteOrderCommand;
import org.example.orderservice.command.CreateOrderCommand;
import org.example.orderservice.command.RequestOrderCancellationCommand;
import org.example.orderservice.entity.Order;
import org.example.orderservice.exception.InvalidOrderDataException;
import org.example.orderservice.exception.OrderLifecycleViolationException;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class OrderCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final OrderRepository orderRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {
            Class<?> payloadType = command.getPayloadType();
            if (payloadType.equals(CreateOrderCommand.class)) {
                validateCreateOrder((CreateOrderCommand) command.getPayload());
            } else if (payloadType.equals(CancelOrderCommand.class)) {
                validateCancelOrder((CancelOrderCommand) command.getPayload());
            } else if(payloadType.equals(CompleteOrderCommand.class)) {
                validateCompleteOrder((CompleteOrderCommand) command.getPayload());
            } else if(payloadType.equals(RequestOrderCancellationCommand.class)) {
                validateRequestOrderCancellation((RequestOrderCancellationCommand) command.getPayload());
            }
            return command;
        };
    }

    private void validateRequestOrderCancellation(RequestOrderCancellationCommand command) {
        if (!orderRepository.existsById(command.getOrderId())) {
            throw new ResourceNotFoundException("Order with ID " + command.getOrderId() + " does not exist.");
        }
    }

    private void validateCompleteOrder(CompleteOrderCommand command) {
        orderRepository.findById(command.getOrderId()).ifPresent(order -> {
            if (order.getStatus() == OrderStatus.CANCELLED) {
                throw new OrderLifecycleViolationException("Cannot complete a cancelled order.");
            }
            if (order.getStatus() == OrderStatus.COMPLETED) {
                throw new ResourceAlreadyExistsException("Order is already completed.");
            }
        });
    }

    private void validateCreateOrder(CreateOrderCommand command) {
        if (orderRepository.existsById(command.getOrderId())) {
            throw new ResourceAlreadyExistsException("Order with ID " + command.getOrderId() + " already exists.");
        }

        validateOrderItems(command.getItems());
        validateTotalAmount(command.getTotalAmount());
    }


    private void validateCancelOrder(CancelOrderCommand command) {
        Order existingOrder = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + command.getOrderId() + " does not exist."));

        if (existingOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderLifecycleViolationException("Order is already cancelled.");
        }

        if (existingOrder.getStatus() == OrderStatus.COMPLETED) {
            throw new OrderLifecycleViolationException("Cannot cancel a completed order.");
        }
    }

    private void validateOrderItems(List<OrderItemDto> items) {

        for (OrderItemDto item : items) {
            if (item.getQuantity() <= 0 || item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidOrderDataException("Each item must have quantity > 0 and a valid price.");
            }
        }
    }

    private void validateTotalAmount(BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOrderDataException("Total amount must be greater than zero.");
        }
    }
}
