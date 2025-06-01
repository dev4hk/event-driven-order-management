package org.example.customerservice.exception;

import org.example.common.dto.ErrorResponseDto;
import org.example.common.exception.GlobalExceptionHandler;
import org.example.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomerExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(InvalidCustomerStateException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCustomerStateException(InvalidCustomerStateException ex, WebRequest request) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .path(request.getDescription(false))
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
