package com.axlero.dashboard.Criteria_API.security;

import com.axlero.dashboard.Criteria_API.entity.User;
import com.axlero.dashboard.Criteria_API.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String displayName;
    private final String password;
    private final UserStatus status;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.password = user.getPassword();
        this.status = user.getStatus();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Option A: only a global, platform-level authority. Workspace roles (WorkspaceMember.role)
     * are NOT put here because a user has a different role in each workspace; they are checked
     * per request against the workspace in the URL.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    /** Login identifier is the email (contract: login by email). */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** Locked when an admin suspended the account or it was locked automatically. */
    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.SUSPENDED && status != UserStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** Disabled when the account was deactivated. */
    @Override
    public boolean isEnabled() {
        return status != UserStatus.INACTIVE;
    }
}
