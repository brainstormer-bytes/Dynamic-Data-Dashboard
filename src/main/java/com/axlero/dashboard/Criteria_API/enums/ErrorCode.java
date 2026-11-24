package com.axlero.dashboard.Criteria_API.enums;

/** Stable machine-readable error codes (openapi: ErrorResponse.error). */
public enum ErrorCode {
    VALIDATION_FAILED,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    CONFLICT,
    INTERNAL_ERROR,
    RATE_LIMITED;

    /** Maps an HTTP status to a contract code. Any other 4xx (405, 415, ...) is reported as VALIDATION_FAILED. */
    public static ErrorCode fromStatus(int status) {
        return switch (status) {
            case 401 -> UNAUTHORIZED;
            case 403 -> FORBIDDEN;
            case 404 -> NOT_FOUND;
            case 409 -> CONFLICT;
            case 249 -> RATE_LIMITED;
            default -> status >= 500 ? INTERNAL_ERROR : VALIDATION_FAILED;
        };
    }
}
