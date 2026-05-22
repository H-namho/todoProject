package com.example.memorypratice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        String message = "요청 값이 올바르지 않습니다.";

        if (e.getBindingResult().hasFieldErrors()) {
            message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException e,
            HttpServletRequest request
    ) {
        String message = "요청 값이 올바르지 않습니다.";

        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            message = violation.getMessage();
            break;
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException e,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(
            UsernameNotFoundException e,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(
            NoSuchElementException e,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(response);
    }
}
