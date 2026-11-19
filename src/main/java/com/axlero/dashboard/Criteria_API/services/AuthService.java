package com.axlero.dashboard.Criteria_API.services;

import com.axlero.dashboard.Criteria_API.dto.LoginRequest;
import com.axlero.dashboard.Criteria_API.dto.LoginResponse;
import com.axlero.dashboard.Criteria_API.entity.User;
import com.axlero.dashboard.Criteria_API.exception.ApiException;
import com.axlero.dashboard.Criteria_API.repository.UserRepository;
import com.axlero.dashboard.Criteria_API.security.CustomUserDetails;
import com.axlero.dashboard.Criteria_API.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * AuthenticationManager -> DaoAuthenticationProvider -> CustomUserDetailsService.loadUserByUsername()
     * -> PasswordEncoder.matches() -> account status checks -> authenticated token. Then JWTs are issued.
     * Bad credentials / locked / disabled accounts throw and are mapped in GlobalExceptionHandler.
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username().trim(), request.password()));
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(principal);
        User user = userRepository.getReferenceById(principal.getId());
        return new LoginResponse(accessToken, "Bearer",
                jwtService.getAccessExpirationMs() / 1000, principal.getId(), principal.getUsername());
    }
}

