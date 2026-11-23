package com.axlero.dashboard.Criteria_API.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Locale;

/** Contract: openapi LoginRequest (login is by email). */
public record LoginRequest(
        @NotBlank(message = "email is required")
        @Size(max = 254, message = "email must be at most 254 characters")
        @Pattern(regexp = ValidationPatterns.EMAIL, message = "email must be a valid address")
        String email,

        @NotBlank(message = "password is required")
        String password) {

    public LoginRequest {
        email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}