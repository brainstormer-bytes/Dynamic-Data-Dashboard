package com.axlero.dashboard.Criteria_API.controller;

import com.axlero.dashboard.Criteria_API.dto.LoginRequest;
import com.axlero.dashboard.Criteria_API.dto.LoginResponse;
import com.axlero.dashboard.Criteria_API.dto.RegisterRequest;
import com.axlero.dashboard.Criteria_API.dto.RegisterResponse;
import com.axlero.dashboard.Criteria_API.services.AuthService;
import com.axlero.dashboard.Criteria_API.services.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterService registerService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
