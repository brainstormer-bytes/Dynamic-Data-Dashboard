package com.axlero.dashboard.Criteria_API.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Locale;

/** Contract: openapi RegisterRequest. The 72-byte BCrypt limit is checked in RegisterService. */
public record RegisterRequest(
        @NotBlank(message = "email is required")
        @Size(max = 254, message = "email must be at most 254 characters")
        @Pattern(regexp = ValidationPatterns.EMAIL, message = "email must be a valid address")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be at least 8 characters")
        String password,

        @NotBlank(message = "displayName is required")
        @Size(max = 100, message = "displayName must be at most 100 characters")
        String displayName) {

    public RegisterRequest {
        email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
        displayName = displayName == null ? null : displayName.trim();
    }
}
