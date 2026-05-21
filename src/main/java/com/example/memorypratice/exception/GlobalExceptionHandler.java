package com.example.memorypratice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        List<FieldErrorResponse> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorResponse(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "요청 값이 올바르지 않습니다.",
                request,
                fieldErrors
        );
    }

    // athVariable 등에 붙은 검증 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException e,
            HttpServletRequest request
    ) {
        List<FieldErrorResponse> fieldErrors = e.getConstraintViolations()
                .stream()
                .map(violation -> new FieldErrorResponse(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()
                ))
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "요청 값이 올바르지 않습니다.",
                request,
                fieldErrors
        );
    }

    // 서비스 로직에서 잘못된 요청 값으로 판단한 경우
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException e,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    // userId나 username으로 회원을 찾지 못한 경우
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(
            UsernameNotFoundException e,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    // todoId 등으로 요청한 데이터를 찾지 못한 경우
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

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<FieldErrorResponse> fieldErrors
    ) {
        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(status).body(response);
    }
}
