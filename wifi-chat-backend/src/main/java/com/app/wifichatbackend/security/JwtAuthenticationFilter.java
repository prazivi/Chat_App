package com.app.wifichatbackend.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter = this filter runs ONCE per request (not per redirect)

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // STEP 1: Get the token from the request header
        //         Header looks like: "Authorization: Bearer eyJhbGci..."
        String token = getTokenFromRequest(request);

        // STEP 2: If token exists and is valid, authenticate the user
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {

            // STEP 3: Get username from the token
            String username = tokenProvider.getUsernameFromToken(token);

            // STEP 4: Load full user details from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // STEP 5: Create an authentication object
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,                    // The user
                            null,                           // Credentials (not needed, token already validated)
                            userDetails.getAuthorities()    // Roles/permissions
                    );
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // STEP 6: Tell Spring Security "this user is authenticated"
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // STEP 7: Continue to the next filter / controller
        filterChain.doFilter(request, response);
    }

    // Helper: extract token from "Authorization: Bearer <token>" header
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);    // Remove "Bearer " prefix
        }
        return null;
    }
}
