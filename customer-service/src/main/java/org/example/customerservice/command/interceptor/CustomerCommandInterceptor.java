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
import org.example.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class CustomerCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final CustomerRepository customerRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {
            if(command.getPayloadType().equals(CreateCustomerCommand.class)) {
                CreateCustomerCommand createCustomerCommand = (CreateCustomerCommand) command.getPayload();
                Optional<Customer> customer = customerRepository.findByEmailAndActive(createCustomerCommand.getEmail(), true);
                if(customer.isPresent()) {
                    throw new ResourceAlreadyExistsException("Customer with email " + createCustomerCommand.getEmail() + " already exists");
                }
            } else if(command.getPayloadType().equals(UpdateCustomerCommand.class)) {
                UpdateCustomerCommand updateCustomerCommand = (UpdateCustomerCommand) command.getPayload();
                Optional<Customer> customer = customerRepository.findByCustomerIdAndActive(updateCustomerCommand.getCustomerId(), true);
                if(customer.isEmpty()) {
                    throw new ResourceNotFoundException("Customer with id " + updateCustomerCommand.getCustomerId() + " not found");
                }
            } else if(command.getPayloadType().equals(DeleteCustomerCommand.class)) {
                DeleteCustomerCommand deleteCustomerCommand = (DeleteCustomerCommand) command.getPayload();
                Optional<Customer> customer = customerRepository.findByCustomerIdAndActive(deleteCustomerCommand.getCustomerId(), true);
                if(customer.isEmpty()) {
                    throw new ResourceNotFoundException("Customer with id " + deleteCustomerCommand.getCustomerId() + " not found");
                }
            }
            return command;
        };
    }
}
