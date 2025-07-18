package org.example.shippingservice.exception;

import org.example.common.dto.ErrorResponseDto;
import org.example.common.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ShippingExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(InvalidShippingStateException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidShippingStateExceptions(InvalidShippingStateException ex, WebRequest request) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .path(request.getDescription(false))
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidShippingDataException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidShippingDataException(InvalidShippingDataException ex, WebRequest request) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .path(request.getDescription(false))
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
