package com.axlero.dashboard.Criteria_API.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs on every request. Reads the Bearer access token and, if valid, puts the authenticated
 * user in the SecurityContext (SecurityContextHolder -> ThreadLocal -> SecurityContext -> Authentication).
 * It never rejects the request itself: it only records the reason, and AuthenticationEntryPoint
 * produces the 401 for protected endpoints.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_ERROR_ATTR = "jwt.auth.error";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        try {
            Claims claims = jwtService.parseClaims(token);
            if (!jwtService.isType(claims, JwtService.TYPE_ACCESS)) {
                throw new JwtException("Not an access token"); // e.g. refresh token used as access token
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());
                if (user.isEnabled() && user.isAccountNonLocked()) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    request.setAttribute(AUTH_ERROR_ATTR, "Account is locked or inactive");
                }
            }
        } catch (ExpiredJwtException e) {
            request.setAttribute(AUTH_ERROR_ATTR, "Access token expired");
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            request.setAttribute(AUTH_ERROR_ATTR, "Invalid access token");
        }

        filterChain.doFilter(request, response);
    }
}
