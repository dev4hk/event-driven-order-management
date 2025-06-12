package org.example.shippingservice.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.example.common.commands.InitiateShippingCommand;
import org.example.common.commands.ProcessShippingCommand;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.shippingservice.command.DeliverShippingCommand;
import org.example.shippingservice.entity.Shipping;
import org.example.shippingservice.exception.InvalidShippingDataException;
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
            if (payload instanceof InitiateShippingCommand) {
                validateInitiateShippingCommand((InitiateShippingCommand) payload);
            }
            if (payload instanceof ProcessShippingCommand) {
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

    private void validateInitiateShippingCommand(InitiateShippingCommand command) {
        if (command.getShippingId() == null) {
            throw new InvalidShippingDataException("Shipping ID must not be null.");
        }
        if (shippingRepository.existsById(command.getShippingId())) {
            throw new ResourceAlreadyExistsException("Shipping with ID " + command.getShippingId() + " already exists.");
        }
        if (command.getOrderId() == null) {
            throw new InvalidShippingDataException("Order ID must not be null.");
        }
        if (command.getShippingDetails() == null) {
            throw new InvalidShippingDataException("Shipping details must not be null.");
        }
        if(
                command.getShippingDetails().getName() == null
                        || command.getShippingDetails().getAddress() == null
                        || command.getShippingDetails().getCity() == null
                        || command.getShippingDetails().getState() == null
                        || command.getShippingDetails().getZipCode() == null
        ) {
            throw new InvalidShippingDataException("Shipping details must not be null.");
        }
    }

    private void validateProcessShipping(ProcessShippingCommand command) {
        if (command.getShippingId() == null) {
            throw new InvalidShippingDataException("Shipping ID must not be null.");
        }
        if (command.getOrderId() == null) {
            throw new InvalidShippingDataException("Order ID must not be null.");
        }
        if (command.getShippingDetails() == null) {
            throw new InvalidShippingDataException("Shipping details must not be null.");
        }
        if (shippingRepository.existsById(command.getShippingId())) {
            throw new ResourceAlreadyExistsException("Shipping with ID " + command.getShippingId() + " already exists.");
        }
    }

    private void validateDeliverShipping(DeliverShippingCommand command) {
        if (command.getShippingId() == null) {
            throw new InvalidShippingDataException("Shipping ID must not be null for delivery.");
        }
        getExistingShipping(command.getShippingId());
    }
}