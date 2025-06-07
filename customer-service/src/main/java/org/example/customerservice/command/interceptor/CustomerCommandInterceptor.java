package org.example.customerservice.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException;
import org.example.customerservice.command.CreateCustomerCommand;
import org.example.customerservice.command.DeleteCustomerCommand;
import org.example.customerservice.command.UpdateCustomerCommand;
import org.example.customerservice.entity.Customer;
import org.example.customerservice.exception.InvalidCustomerDataException;
import org.example.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class CustomerCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final CustomerRepository customerRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {
            Object payload = command.getPayload();

            if (payload instanceof CreateCustomerCommand) {
                validateCreateCustomer((CreateCustomerCommand) payload);
            } else if (payload instanceof UpdateCustomerCommand) {
                validateUpdateCustomer((UpdateCustomerCommand) payload);
            } else if (payload instanceof DeleteCustomerCommand) {
                validateDeleteCustomer((DeleteCustomerCommand) payload);
            }

            return command;
        };
    }

    private Customer getActiveCustomerById(UUID customerId) {
        return customerRepository.findByCustomerIdAndActive(customerId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Active customer with ID " + customerId + " not found."));
    }

    private void validateCreateCustomer(CreateCustomerCommand command) {
        if (command.getCustomerId() == null || command.getName() == null || command.getEmail() == null) {
            throw new InvalidCustomerDataException("Customer ID, Name, and Email must not be null for creation.");
        }

        if (customerRepository.findByEmailAndActive(command.getEmail(), true).isPresent()) {
            throw new ResourceAlreadyExistsException("Active customer with email '" + command.getEmail() + "' already exists.");
        }
    }

    private void validateUpdateCustomer(UpdateCustomerCommand command) {
        Customer existingCustomer = getActiveCustomerById(command.getCustomerId());
    }

    private void validateDeleteCustomer(DeleteCustomerCommand command) {
        getActiveCustomerById(command.getCustomerId());
    }
}