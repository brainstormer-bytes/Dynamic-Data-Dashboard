package com.axlero.dashboard.Criteria_API.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** 401: the caller is NOT authenticated (missing/expired/invalid token) and hit a protected endpoint. */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Object reason = request.getAttribute(JwtAuthenticationFilter.AUTH_ERROR_ATTR);
        String message = reason != null ? reason.toString() : "Authentication required. Provide a valid Bearer token";

        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        SecurityErrorWriter.write(request, response, HttpStatus.UNAUTHORIZED, message);
    }
}
