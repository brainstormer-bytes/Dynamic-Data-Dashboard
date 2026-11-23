package com.axlero.dashboard.Criteria_API.dto;
import com.axlero.dashboard.Criteria_API.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

/** Contract: openapi ErrorResponse. timestamp is an ISO-8601 UTC string, fieldErrors is never null. */
public record ErrorResponse(int status,
                            ErrorCode error,
                            String message,
                            String path,
                            String timestamp,
                            List<FieldError> fieldErrors) {

    public record FieldError(String field, String message) {
    }

    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return of(status, message, path, List.of());
    }

    public static ErrorResponse of(HttpStatus status, String message, String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(
                status.value(),
                ErrorCode.fromStatus(status.value()),
                message,
                path,
                Instant.now().toString(),
                fieldErrors == null ? List.of() : List.copyOf(fieldErrors));
    }
}
