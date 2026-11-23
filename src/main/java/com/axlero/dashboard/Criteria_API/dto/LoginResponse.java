package com.axlero.dashboard.Criteria_API.dto;

/**
 * Contract: openapi LoginResponse. refreshToken is the one addition to the original contract
 * (needed by /auth/refresh and /auth/logout, now documented in openapi 2.1.0).
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn, // access token lifetime in seconds
        UserResponse user) {
}
