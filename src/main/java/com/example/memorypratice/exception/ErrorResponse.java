package com.example.memorypratice.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorResponse> fieldErrors
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, List.of());
    }

    public static ErrorResponse of(int status, String error, String message, String path,
                                   List<FieldErrorResponse> fieldErrors) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, fieldErrors);
    }
}
