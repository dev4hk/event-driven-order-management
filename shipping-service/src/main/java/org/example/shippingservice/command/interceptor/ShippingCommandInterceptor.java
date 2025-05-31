package org.example.shippingservice.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.example.common.constants.ShippingStatus;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.common.commands.CreateShippingCommand;
import org.example.shippingservice.command.ProcessShippingCommand;
import org.example.shippingservice.exception.InvalidShippingStateException;
import org.example.shippingservice.repository.ShippingRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class ShippingCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final ShippingRepository shippingRepository;

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {

            if (CreateShippingCommand.class.equals(command.getPayloadType())) {
                CreateShippingCommand createCmd = (CreateShippingCommand) command.getPayload();

                if (shippingRepository.existsByOrderId(createCmd.getOrderId())) {
                    throw new ResourceAlreadyExistsException("Shipping already exists for orderId: " + createCmd.getOrderId());
                }

                if (shippingRepository.existsById(createCmd.getShippingId())) {
                    throw new ResourceAlreadyExistsException("Shipping with ID already exists: " + createCmd.getShippingId());
                }
            }

            if (ProcessShippingCommand.class.equals(command.getPayloadType())) {
                ProcessShippingCommand updateCmd = (ProcessShippingCommand) command.getPayload();

                var shipping = shippingRepository.findById(updateCmd.getShippingId());
                if (shipping.isEmpty()) {
                    throw new ResourceNotFoundException("Cannot update status. Shipping not found for ID: " + updateCmd.getShippingId());
                }

                if (shipping.get().getStatus() == ShippingStatus.DELIVERED) {
                    throw new InvalidShippingStateException("Cannot update status of delivered shipping.");
                }
            }

            return command;
        };
    }

}
