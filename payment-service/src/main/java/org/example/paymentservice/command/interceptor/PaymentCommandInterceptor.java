package org.example.paymentservice.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.example.common.commands.CancelPaymentCommand;
import org.example.common.commands.InitiatePaymentCommand;
import org.example.common.commands.ProcessPaymentCommand;
import org.example.common.constants.PaymentStatus;
import org.example.common.exception.ResourceAlreadyExistsException;
import org.example.common.exception.ResourceNotFoundException; // Corrected import
import org.example.paymentservice.entity.Payment;
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

    private void validateInitiatePaymentCommand(InitiatePaymentCommand command) {
        validateCorePaymentData(command.getPaymentId(), command.getOrderId(), command.getCustomerId(), command.getTotalAmount());
        if(
                command.getCustomerName() == null
                || command.getCustomerEmail() == null
                || command.getAddress() == null
                || command.getCity() == null
                || command.getState() == null
                || command.getZipCode() == null
        ) {
            throw new InvalidPaymentDataException("Customer name, email, address, city, state, and zip code must not be null.");
        }
        if (paymentRepository.existsById(command.getPaymentId())) {
            throw new ResourceAlreadyExistsException("Payment with this ID already exists: " + command.getPaymentId());
        }
    }

    private void validateProcessPaymentCommand(ProcessPaymentCommand command) {
        validateCorePaymentData(command.getPaymentId(), command.getOrderId(), command.getCustomerId(), command.getTotalAmount());

        Payment payment = paymentRepository.findById(command.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment with this ID does not exist: " + command.getPaymentId()));

        if(payment.getPaymentStatus().equals(PaymentStatus.COMPLETED) || payment.getPaymentStatus().equals(PaymentStatus.CANCELLED)) {
            throw new InvalidPaymentStateException("Payment with ID " + command.getPaymentId() + " is already completed or cancelled.");
        }
        if (command.getTotalAmount().compareTo(payment.getTotalAmount()) != 0) {
            throw new InvalidPaymentDataException("Amount in command must be equal to the original payment amount.");
        }
    }

    private void validateCancelPaymentCommand(CancelPaymentCommand command) {
        validateCorePaymentData(command.getPaymentId(), command.getOrderId(), command.getCustomerId(), command.getAmount());

        if (!paymentRepository.existsById(command.getPaymentId())) {
            throw new ResourceNotFoundException("Payment with this ID does not exist: " + command.getPaymentId());
        }
    }
}