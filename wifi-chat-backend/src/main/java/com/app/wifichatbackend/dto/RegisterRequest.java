package com.app.wifichatbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data    // Lombok: getters + setters + toString
public class RegisterRequest {

    @NotBlank(message = "Username is required")          // Can't be null or ""
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")    // Must be xxx@yyy.zzz format
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String displayName;   // Optional
}