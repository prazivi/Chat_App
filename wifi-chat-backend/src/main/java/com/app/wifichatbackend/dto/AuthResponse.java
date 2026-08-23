package com.app.wifichatbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;          // The JWT token
    private String tokenType;      // Always "Bearer"
    private String username;       // The logged-in username
}
