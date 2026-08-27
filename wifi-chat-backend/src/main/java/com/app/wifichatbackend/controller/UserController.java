package com.app.wifichatbackend.controller;

import com.app.wifichatbackend.model.User;
import com.app.wifichatbackend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile and online status")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;

    // GET /api/users/me — requires JWT
    @GetMapping("/me")
    @Operation(summary = "Get current user profile",
            description = "Returns the profile of the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile returned"),
            @ApiResponse(responseCode = "401", description = "JWT token missing or invalid")
    })
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        // 'principal' is automatically injected by Spring Security
        // It contains the authenticated user's info (set by our JwtAuthenticationFilter)

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return user info (but NOT the password hash!)
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "displayName", user.getDisplayName(),
                "status", user.getStatus(),
                "createdAt", user.getCreatedAt().toString()
        ));
    }

    // GET /api/users/online — requires JWT
    @GetMapping("/online")
    @Operation(summary = "Get all online users",
            description = "Returns a list of users whose status is ONLINE")
    public ResponseEntity<List<User>> getOnlineUsers() {
        return ResponseEntity.ok(
                userRepository.findByStatus(User.UserStatus.ONLINE)
        );
    }
}
