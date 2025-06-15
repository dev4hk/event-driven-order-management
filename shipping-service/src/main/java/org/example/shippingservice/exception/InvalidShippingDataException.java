package org.example.shippingservice.exception;

public class InvalidShippingDataException extends RuntimeException {
    public InvalidShippingDataException(String message) {
        super(message);
    }
}
