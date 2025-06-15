package org.example.paymentservice.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.example.common.commands.CancelPaymentCommand;
import org.example.paymentservice.command.InitiatePaymentCommand;
import org.example.common.commands.ProcessPaymentCommand;
import org.example.common.commands.RollBackPaymentStatusCommand;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException; // Corrected import
import org.example.paymentservice.exception.InvalidPaymentDataException;
import org.example.paymentservice.exception.InvalidPaymentStateException;
import org.example.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class PaymentCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final PaymentRepository paymentRepository;

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {
            Object payload = command.getPayload();
            if (payload instanceof InitiatePaymentCommand) {
                validateInitiatePaymentCommand((InitiatePaymentCommand) payload);
            } else if (payload instanceof ProcessPaymentCommand) {
                validateProcessPaymentCommand((ProcessPaymentCommand) payload);
            } else if (payload instanceof CancelPaymentCommand) {
                validateCancelPaymentCommand((CancelPaymentCommand) payload);
            } else if (payload instanceof RollBackPaymentStatusCommand) {
                validateRollbackPaymentStatusCommand((RollBackPaymentStatusCommand) payload);
            }

            return command;
        };
    }

    private void validateCorePaymentData(UUID paymentId, UUID orderId, UUID customerId, BigDecimal amount) {
        if (paymentId == null || orderId == null || customerId == null) {
            throw new InvalidPaymentDataException("Payment ID, order ID, and customer ID must not be null.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentDataException("Amount must be greater than zero.");
        }
    }

    private void validateInitiatePaymentCommand(InitiatePaymentCommand payload) {
        validateCorePaymentData(payload.getPaymentId(), payload.getOrderId(), payload.getCustomerId(), payload.getTotalAmount());
        if (paymentRepository.existsById(payload.getPaymentId())) {
            throw new ResourceAlreadyExistsException("Payment with this ID already exists: " + payload.getPaymentId());
        }
    }

    private void validateProcessPaymentCommand(ProcessPaymentCommand command) {
        validateCorePaymentData(command.getPaymentId(), command.getOrderId(), command.getCustomerId(), command.getTotalAmount());

        if (paymentRepository.existsById(command.getPaymentId())) {
            throw new ResourceAlreadyExistsException("Payment with this ID already exists: " + command.getPaymentId());
        }
    }

    private void validateCancelPaymentCommand(CancelPaymentCommand command) {
        validateCorePaymentData(command.getPaymentId(), command.getOrderId(), command.getCustomerId(), command.getAmount());

        if (!paymentRepository.existsById(command.getPaymentId())) {
            throw new ResourceNotFoundException("Payment with this ID does not exist: " + command.getPaymentId());
        }
    }

    private void validateRollbackPaymentStatusCommand(RollBackPaymentStatusCommand command) {
        if(command.getPaymentId() == null || command.getOrderId() == null || command.getPaymentStatus() == null) {
            throw new InvalidPaymentStateException("Payment ID, order ID, and payment status must not be null.");
        }
        if (!paymentRepository.existsById(command.getPaymentId())) {
            throw new ResourceNotFoundException("Payment with this ID does not exist: " + command.getPaymentId());
        }
    }

}