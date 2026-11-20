package com.axlero.dashboard.Criteria_API.services;

import com.axlero.dashboard.Criteria_API.dto.RegisterRequest;
import com.axlero.dashboard.Criteria_API.dto.RegisterResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RegisterService {

    // Letters and digits only: no special characters, spaces or emojis. Length 3-30.
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9]{3,30}$");

    // General email pattern (ASCII only, so no emojis), plus a max length check below.
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");
    private static final int EMAIL_MAX_LENGTH = 254;

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_BYTES = 72; // BCrypt ignores/rejects anything beyond 72 bytes

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String username = request.username() == null ? null : request.username().trim();
        String email = request.email() == null ? null : request.email().trim().toLowerCase(Locale.ROOT);
        String password = request.password();

        validate(username, email, password);

        if (userRepository.existsByUsername(username)) {
            throw ApiException.conflict("Username is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw ApiException.conflict("Email is already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(UserStatus.ACTIVE);

        try {
            user = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) { // two requests racing past the exists checks
            throw ApiException.conflict("Username or email already exists");
        }

        return new RegisterResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getStatus(), user.getCreatedAt());
    }

    private void validate(String username, String email, String password) {
        List<String> errors = new ArrayList<>();

        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            errors.add("Username must be 3-30 characters, letters and digits only (no special characters or emojis)");
        }

        if (email == null || email.length() > EMAIL_MAX_LENGTH || !EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("Email must be a valid address of at most " + EMAIL_MAX_LENGTH + " characters");
        }

        if (password == null || password.length() < PASSWORD_MIN_LENGTH
                || password.getBytes(StandardCharsets.UTF_8).length > PASSWORD_MAX_BYTES) {
            errors.add("Password must be at least " + PASSWORD_MIN_LENGTH + " characters and at most "
                    + PASSWORD_MAX_BYTES + " bytes");
        }

        if (!errors.isEmpty()) {
            throw ApiException.badRequest(String.join("; ", errors));
        }
    }
}

