package org.example.paymentservice.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.commands.CreatePaymentCommand;
import org.example.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class PaymentCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final PaymentRepository paymentRepository;

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {

            if (command.getPayloadType().equals(CreatePaymentCommand.class)) {
                CreatePaymentCommand payload = (CreatePaymentCommand) command.getPayload();

                if (paymentRepository.existsById(payload.getPaymentId())) {
                    throw new ResourceAlreadyExistsException("Payment with this ID already exists: " + payload.getPaymentId());
                }
            }

            return command;
        };
    }
}
