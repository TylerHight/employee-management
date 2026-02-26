package com.ibm.fscc.loginservice.services;

import static org.junit.jupiter.api.Assertions.*;

import javax.naming.AuthenticationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ibm.fscc.loginservice.dto.AuthResponseDto;

@SpringBootTest
public class LoginServiceTests {

    @Autowired
    private LoginService loginService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Test
    public void testValidAuthentication() throws Exception {
        // Given we have a user with valid credentials
        // This relies on having matching test data in the test database
        
        // When
        AuthResponseDto response = loginService.authenticateUser("test@example.com", "password123");
        
        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("test@example.com", response.getEmail());
    }
    
    @Test
    public void testInvalidPassword() {
        // When/Then
        assertThrows(AuthenticationException.class, () -> {
            loginService.authenticateUser("test@example.com", "wrongpassword");
        });
    }
    
    @Test
    public void testNonExistentUser() {
        // When/Then
        assertThrows(AuthenticationException.class, () -> {
            loginService.authenticateUser("nonexistent@example.com", "password123");
        });
    }
    
    @Test
    public void testPasswordEncoding() {
        // Given
        String rawPassword = "password123";
        
        // When
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // Then
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
}
