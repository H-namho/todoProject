package com.example.memorypratice.exception;

public record FieldErrorResponse(
        String field,
        String message,
        Object rejectedValue
) {
}
