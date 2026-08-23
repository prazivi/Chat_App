package com.app.wifichatbackend.controller;

import com.app.wifichatbackend.dto.AuthResponse;
import com.app.wifichatbackend.dto.LoginRequest;
import com.app.wifichatbackend.dto.RegisterRequest;
import com.app.wifichatbackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController                      // Tells Spring: this handles HTTP requests, returns JSON
@RequestMapping("/api/auth")         // All endpoints start with /api/auth
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        //                             ↑ @Valid triggers the @NotBlank, @Email etc. validations
        //                               @RequestBody parses the JSON body into RegisterRequest

        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)           // 201 Created
                .body(Map.of("message", "User registered successfully!"));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);           // 200 OK + { token, tokenType, username }
    }
}
