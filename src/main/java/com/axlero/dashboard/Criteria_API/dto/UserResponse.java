package com.axlero.dashboard.Criteria_API.dto;

import com.axlero.dashboard.Criteria_API.entity.User;
import com.axlero.dashboard.Criteria_API.enums.Role;

/** Contract: openapi UserResponse. role is the platform role, constant USER (not stored). */
public record UserResponse(Long id, String email, String displayName, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getDisplayName(), Role.USER);
    }
}
