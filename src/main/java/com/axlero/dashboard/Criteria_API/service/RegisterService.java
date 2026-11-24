package com.axlero.dashboard.Criteria_API.service;

import com.axlero.dashboard.Criteria_API.dto.RegisterRequest;
import com.axlero.dashboard.Criteria_API.dto.UserResponse;
import com.axlero.dashboard.Criteria_API.entity.User;
import com.axlero.dashboard.Criteria_API.enums.UserStatus;
import com.axlero.dashboard.Criteria_API.exception.ApiException;
import com.axlero.dashboard.Criteria_API.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private static final int PASSWORD_MAX_BYTES = 72; // BCrypt ignores/rejects anything beyond 72 bytes

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Field-level rules (email format/length, password min length, displayName length) are Bean Validation
     * annotations on RegisterRequest, checked by @Valid in the controller (-> 400 with fieldErrors).
     * Only the byte-based BCrypt limit lives here.
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > PASSWORD_MAX_BYTES) {
            throw ApiException.invalidField("password", "password must be at most " + PASSWORD_MAX_BYTES + " bytes");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw ApiException.conflict("Email is already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setDisplayName(request.displayName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);

        try {
            user = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) { // two requests racing past the exists check
            throw ApiException.conflict("Email is already registered");
        }
        return UserResponse.from(user);
    }
}

