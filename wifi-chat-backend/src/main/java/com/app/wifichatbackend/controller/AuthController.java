package com.app.wifichatbackend.controller;

import com.app.wifichatbackend.dto.AuthResponse;
import com.app.wifichatbackend.dto.LoginRequest;
import com.app.wifichatbackend.dto.RegisterRequest;
import com.app.wifichatbackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController                      // Tells Spring: this handles HTTP requests, returns JSON
@RequestMapping("/api/auth")         // All endpoints start with /api/auth
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login to get a JWT token")
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account. Username and email must be unique. "
                    + "Password is hashed with BCrypt before storage."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicate username/email")
    })
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
    @Operation(
            summary = "Login and get JWT token",
            description = "Authenticates the user and returns a JWT token valid for 24 hours. "
                    + "Include this token in the Authorization header for all protected endpoints."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);           // 200 OK + { token, tokenType, username }
    }
}
