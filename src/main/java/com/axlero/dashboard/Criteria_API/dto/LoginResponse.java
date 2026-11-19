package com.axlero.dashboard.Criteria_API.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn, // access token lifetime in seconds
        Long userId,
        String username) {
}
