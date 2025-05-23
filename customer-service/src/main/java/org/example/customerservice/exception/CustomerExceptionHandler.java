package org.example.customerservice.exception;

import org.example.common.exception.GlobalExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomerExceptionHandler extends GlobalExceptionHandler {
}
