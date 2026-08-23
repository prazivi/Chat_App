package com.app.wifichatbackend.controller;



import com.app.wifichatbackend.model.User;
import com.app.wifichatbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // GET /api/users/me — requires JWT
    @GetMapping("/me")
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
    public ResponseEntity<List<User>> getOnlineUsers() {
        return ResponseEntity.ok(
                userRepository.findByStatus(User.UserStatus.ONLINE)
        );
    }
}
