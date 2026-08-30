package com.app.wifichatbackend.service;


import com.app.wifichatbackend.dto.AuthResponse;
import com.app.wifichatbackend.dto.LoginRequest;
import com.app.wifichatbackend.dto.RegisterRequest;
import com.app.wifichatbackend.model.User;
import com.app.wifichatbackend.repository.UserRepository;
import com.app.wifichatbackend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;    // BCrypt from SecurityConfig
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ChatService chatService;

    // ──────────────────────────────────────────────
    //  REGISTER: Create a new user
    // ──────────────────────────────────────────────
    public void register(RegisterRequest request) {

        // Check if username is already taken
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username '" + request.getUsername() + "' is already taken");
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email '" + request.getEmail() + "' is already in use");
        }

        // Build the user entity
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                //                  ↑ IMPORTANT: We hash the password with BCrypt
                //                    "MyPassword123" → "$2a$10$xK8f..." (60-char hash)
                //                    The original password is NEVER stored
                .displayName(request.getDisplayName() != null
                        ? request.getDisplayName()
                        : request.getUsername())    // Default display name = username
                .build();

        // Save to database
        userRepository.save(user);
    }

    // ──────────────────────────────────────────────
    //  LOGIN: Verify credentials and return JWT
    // ──────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {

        // This does the actual authentication:
        // 1. Calls CustomUserDetailsService.loadUserByUsername()
        // 2. Compares the BCrypt hash of the provided password with the stored hash
        // 3. Throws BadCredentialsException if they don't match
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // If we get here, authentication was successful
        // Generate a JWT token for this user
        String token = jwtTokenProvider.generateToken(request.getUsername());
        chatService.setUserOnline(request.getUsername());

        return new AuthResponse(token, "Bearer", request.getUsername());
    }
}