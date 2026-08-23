package com.app.wifichatbackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component   // Spring manages this class (you can @Autowire it anywhere)
public class JwtTokenProvider {

    @Value("${jwt.secret}")        // Reads "jwt.secret" from application.yml
    private String jwtSecret;

    @Value("${jwt.expiration}")    // Reads "jwt.expiration" from application.yml
    private long jwtExpiration;

    // ──────────────────────────────────────────────
    //  1. GENERATE a new JWT token
    // ──────────────────────────────────────────────
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(username)           // WHO this token belongs to
                .issuedAt(now)               // WHEN it was created
                .expiration(expiryDate)      // WHEN it expires
                .signWith(getSigningKey())   // Sign with our secret key
                .compact();                  // Build the final string
    }

    // ──────────────────────────────────────────────
    //  2. EXTRACT username from a token
    // ──────────────────────────────────────────────
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())       // Use same key to verify
                .build()
                .parseSignedClaims(token)           // Parse the token
                .getPayload()
                .getSubject();                      // Get the "subject" (username)
    }

    // ──────────────────────────────────────────────
    //  3. VALIDATE a token (is it legit and not expired?)
    // ──────────────────────────────────────────────
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;    // Token is valid!
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token expired: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("JWT error: " + e.getMessage());
            return false;
        }
    }

    // Convert our secret string into a proper SecretKey object
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
