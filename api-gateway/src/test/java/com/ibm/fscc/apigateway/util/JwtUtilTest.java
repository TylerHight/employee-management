package com.ibm.fscc.apigateway.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.Date;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=testSecret123456789012345678901234567890",
        "jwt.expiration=3600000"
})
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private String validToken;

    private SecretKey getTestSigningKey() {
        String secret = "testSecret123456789012345678901234567890";
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @BeforeEach
    void setUp() {
        validToken = Jwts.builder()
                .subject("testuser")
                .claim("userId", "123")
                .claim("role", "ADMIN")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(getTestSigningKey())
                .compact();
    }

    @Test
    public void contextLoads() {
        // Verify Spring can load the JwtUtil bean
        JwtUtil jwtUtil = new JwtUtil();
    }

    @Test
    public void shouldValidateValidToken() {
        assertTrue(jwtUtil.validateToken(validToken));

    }

    @Test
    public void shouldRejectNullToken() {
        String token = null;
        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    public void shouldRejectEmptyToken() {
        String token = "";
        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    public void shouldRejectMalformedToken() {
        String token = "not.a.valid.jwt.token.at.all";
        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    public void shouldExtractClaims() {
        Claims claims = jwtUtil.extractClaims(validToken);

        assertEquals("123", claims.get("userId"));
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    public void shouldExtractUsername() {
        String username = jwtUtil.extractUsername(validToken);
        assertEquals("testuser", username);
    }

}
