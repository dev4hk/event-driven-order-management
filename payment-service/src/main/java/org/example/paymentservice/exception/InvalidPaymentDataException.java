package org.example.paymentservice.exception;

public class InvalidPaymentDataException extends RuntimeException {

    public InvalidPaymentDataException(String message) {
        super(message);
    }

}
