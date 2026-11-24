package com.axlero.dashboard.Criteria_API.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private record Rule(String name, int capacity, Duration period, boolean perUser) {}

    private static final Rule LOGIN = new Rule("login", 10, Duration.ofMinutes(1), false);
    private static final Rule REGISTER = new Rule("register", 10, Duration.ofHours(1), false);
    private static final Rule REFRESH = new Rule("refresh", 30, Duration.ofMinutes(1), false);
    private static final Rule METRICS_QUERY = new Rule("metrics-query", 60, Duration.ofMinutes(1), true);
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private final RateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Rule rule = resolve(request);
        if (rule != null) {
            RateLimiter.Decision decision = rateLimiter.tryConsume(
                    rule.name() + ":" + clientKey(request, rule), rule.capacity(), rule.period());
            if (!decision.allowed()) {
                response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(decision.retryAfterSeconds()));
                SecurityErrorWriter.write(request, response, HttpStatus.TOO_MANY_REQUESTS,
                        "Too many requests. Try again later");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private Rule resolve(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) return null;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return switch (path) {
            case "/api/v1/auth/login" -> LOGIN;
            case "/api/v1/auth/register" -> REGISTER;
            case "/api/v1/auth/refresh" -> REFRESH;
            default -> MATCHER.match("/api/v1/workspaces/*/metrics/query", path) ? METRICS_QUERY : null;
        };
    }

    private String clientKey(HttpServletRequest request, Rule rule) {
        if (rule.perUser()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
                return "user:" + user.getId();
            }
        }
        return "ip:" + request.getRemoteAddr();
    }
}