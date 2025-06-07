package org.example.shippingservice.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.example.common.constants.ShippingStatus;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.common.commands.CreateShippingCommand;
import org.example.shippingservice.command.DeliverShippingCommand;
import org.example.shippingservice.command.ProcessShippingCommand;
import org.example.shippingservice.entity.Shipping;
import org.example.shippingservice.exception.InvalidShippingDataException;
import org.example.shippingservice.exception.InvalidShippingStateException;
import org.example.shippingservice.repository.ShippingRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class ShippingCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final ShippingRepository shippingRepository;

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {
            Object payload = command.getPayload();

            if (payload instanceof CreateShippingCommand) {
                validateCreateShipping((CreateShippingCommand) payload);
            } else if (payload instanceof ProcessShippingCommand) {
                validateProcessShipping((ProcessShippingCommand) payload);
            } else if (payload instanceof DeliverShippingCommand) {
                validateDeliverShipping((DeliverShippingCommand) payload);
            }

            return command;
        };
    }

    private Shipping getExistingShipping(UUID shippingId) {
        return shippingRepository.findById(shippingId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping not found for ID: " + shippingId));
    }

    private void validateCreateShipping(CreateShippingCommand command) {
        if (command.getShippingId() == null ||
                command.getOrderId() == null ||
                command.getCustomerId() == null ||
                command.getAddress() == null ||
                command.getCity() == null ||
                command.getState() == null ||
                command.getZipCode() == null ||
                command.getCustomerName() == null) {
            throw new InvalidShippingDataException("Shipping ID, Order ID, Customer ID, Address, City, State, Zip Code, and Customer Name must not be null.");
        }

        if (shippingRepository.existsByOrderId(command.getOrderId())) {
            throw new ResourceAlreadyExistsException("Shipping already exists for orderId: " + command.getOrderId());
        }
        if (shippingRepository.existsById(command.getShippingId())) {
            throw new ResourceAlreadyExistsException("Shipping with ID " + command.getShippingId() + " already exists.");
        }
    }

    private void validateProcessShipping(ProcessShippingCommand command) {
        if (command.getShippingId() == null) {
            throw new InvalidShippingDataException("Shipping ID must not be null.");
        }

        Shipping existingShipping = getExistingShipping(command.getShippingId());

        if (existingShipping.getStatus() == ShippingStatus.DELIVERED || existingShipping.getStatus() == ShippingStatus.CANCELLED) {
            throw new InvalidShippingStateException("Cannot process a delivered or cancelled shipping with ID: " + command.getShippingId());
        }
    }

    private void validateDeliverShipping(DeliverShippingCommand command) {
        if (command.getShippingId() == null || command.getNewStatus() == null) {
            throw new InvalidShippingDataException("Shipping ID and New Status must not be null for delivery.");
        }

        Shipping existingShipping = getExistingShipping(command.getShippingId());

        if (existingShipping.getStatus() != ShippingStatus.SHIPPED) {
            throw new InvalidShippingStateException("Cannot deliver a shipping with ID " + command.getShippingId() + " that is not in SHIPPED status. Current status: " + existingShipping.getStatus());
        }
    }
}