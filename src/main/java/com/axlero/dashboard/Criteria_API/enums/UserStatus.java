package com.axlero.dashboard.Criteria_API.enums;

public enum UserStatus {
    ACTIVE,     // normal account
    INACTIVE,   // deactivated (disabled) - isEnabled() == false
    SUSPENDED,  // blocked by an admin - isAccountNonLocked() == false
    LOCKED      // locked automatically (e.g. security reasons) - isAccountNonLocked() == false
}
