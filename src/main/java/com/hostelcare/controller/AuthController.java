package com.hostelcare.controller;

import com.hostelcare.dto.request.LoginRequest;
import com.hostelcare.dto.request.RegisterRequest;
import com.hostelcare.dto.response.AuthResponse;
import com.hostelcare.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication endpoints.
 *
 * POST /api/auth/register  → Register a new user
 * POST /api/auth/login     → Login and receive a JWT token
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user.
     *
     * Sample Request Body:
     * {
     *   "name": "John Doe",
     *   "email": "john@hostel.com",
     *   "password": "secret123",
     *   "role": "STUDENT"
     * }
     *
     * Sample Response:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "email": "john@hostel.com",
     *   "name": "John Doe",
     *   "role": "STUDENT"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Login with email and password.
     *
     * Sample Request Body:
     * {
     *   "email": "john@hostel.com",
     *   "password": "secret123"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
