package com.axlero.dashboard.Criteria_API.dto;

import com.axlero.dashboard.Criteria_API.enums.UserStatus;

import java.time.LocalDateTime;

public record RegisterResponse(Long id, String username, String email, UserStatus status, LocalDateTime createdAt) {
}
