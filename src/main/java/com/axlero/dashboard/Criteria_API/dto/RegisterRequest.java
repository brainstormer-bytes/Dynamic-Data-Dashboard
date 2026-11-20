package com.axlero.dashboard.Criteria_API.dto;

// Validation is done in RegisterService (not with annotations).
public record RegisterRequest(String username, String email, String password) {
}
