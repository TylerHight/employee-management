package com.ibm.fscc.loginservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24h for auth tokens
    private Long authExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // === Authorization Token ===
    public String generateAuthToken(String username, Object userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("role", role);
        claims.put("purpose", "AUTH");
        return createToken(claims, username, authExpiration);
    }

    // === Password Reset Token ===
    public String generatePasswordResetToken(String email, long expirySeconds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("purpose", "PASSWORD_RESET");
        return createToken(claims, email, expirySeconds * 1000);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiryMillis) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiryMillis))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
