package com.axlero.dashboard.Criteria_API.services;

import com.axlero.dashboard.Criteria_API.dto.LoginRequest;
import com.axlero.dashboard.Criteria_API.dto.LoginResponse;
import com.axlero.dashboard.Criteria_API.entity.RefreshToken;
import com.axlero.dashboard.Criteria_API.entity.User;
import com.axlero.dashboard.Criteria_API.exception.ApiException;
import com.axlero.dashboard.Criteria_API.repository.UserRepository;
import com.axlero.dashboard.Criteria_API.security.CustomUserDetails;
import com.axlero.dashboard.Criteria_API.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
    private final RefreshTokenService refreshTokenService;
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
        return issueTokens(principal);
    }

    /**
     * Rotation: the used refresh token is revoked and a new pair is issued.
     * If an already-revoked token shows up again it may have been stolen, so every token of that
     * user is revoked. noRollbackFor keeps that revocation when we then throw the 401.
     */
    @Transactional(noRollbackFor = ApiException.class)
    public LoginResponse refresh(String refreshToken) {
        validateRefreshJwt(refreshToken);

        RefreshToken stored = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> ApiException.unauthorized("Invalid refresh token"));

        if (stored.isRevoked()) {
            refreshTokenService.revokeAllForUser(stored.getUser().getId());
            throw ApiException.unauthorized("Refresh token reuse detected. Please log in again");
        }
        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw ApiException.unauthorized("Refresh token expired");
        }

        CustomUserDetails principal = new CustomUserDetails(stored.getUser());
        if (!principal.isEnabled() || !principal.isAccountNonLocked()) {
            throw ApiException.forbidden("Account is locked or inactive");
        }

        refreshTokenService.revoke(stored);
        return issueTokens(principal);
    }

    /** Idempotent: unknown/expired/already-revoked tokens are ignored, logout never fails. */
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.findByToken(refreshToken)
                .filter(t -> !t.isRevoked())
                .ifPresent(refreshTokenService::revoke);
    }

    private void validateRefreshJwt(String token) {
        Claims claims;
        try {
            claims = jwtService.parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw ApiException.unauthorized("Refresh token expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw ApiException.unauthorized("Invalid refresh token");
        }
        if (!jwtService.isType(claims, JwtService.TYPE_REFRESH)) {
            throw ApiException.unauthorized("Invalid refresh token");
        }
    }

    private LoginResponse issueTokens(CustomUserDetails principal) {
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        User user = userRepository.getReferenceById(principal.getId());
        LocalDateTime refreshExpiry = LocalDateTime.now().plus(Duration.ofMillis(jwtService.getRefreshExpirationMs()));
        refreshTokenService.store(user, refreshToken, refreshExpiry);

        return new LoginResponse(accessToken, refreshToken, "Bearer",
                jwtService.getAccessExpirationMs() / 1000, principal.getId(), principal.getUsername());
    }
}

