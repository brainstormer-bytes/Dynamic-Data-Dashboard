package com.axlero.dashboard.Criteria_API.exception;

import com.axlero.dashboard.Criteria_API.dto.ErrorResponse;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final List<ErrorResponse.FieldError> fieldErrors;
    private final Long retryAfterSeconds;

    public ApiException(HttpStatus status, String message) {
        this(status, message, List.of(), null);
    }

    public ApiException(HttpStatus status, String message, List<ErrorResponse.FieldError> fieldErrors) {
        this(status, message, fieldErrors, null);
    }

    private ApiException(HttpStatus status, String message,
                         List<ErrorResponse.FieldError> fieldErrors, Long retryAfterSeconds) {
        super(message);
        this.status = status;
        this.fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
        this.retryAfterSeconds = retryAfterSeconds;
    }


    public HttpStatus getStatus() {
        return status;
    }

    public List<ErrorResponse.FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }

    /** 400 with per-field details, e.g. the query engine: validationFailed(List.of(new FieldError("groupByField", ...))). */
    public static ApiException validationFailed(List<ErrorResponse.FieldError> fieldErrors) {
        return new ApiException(HttpStatus.BAD_REQUEST, "Request has invalid fields", fieldErrors);
    }

    /** Shortcut for a single invalid field, e.g. invalidField("groupByField", "Column \"password\" is not allowed for dataset \"sales\""). */
    public static ApiException invalidField(String field, String message) {
        return validationFailed(List.of(new ErrorResponse.FieldError(field, message)));
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }

    public Long getRetryAfterSeconds() { return retryAfterSeconds; }

    public static ApiException tooManyRequests(String message, long retryAfterSeconds) {
        return new ApiException(HttpStatus.TOO_MANY_REQUESTS, message, List.of(), retryAfterSeconds);
    }
}

