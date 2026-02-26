package com.ibm.fscc.loginservice.services;

import com.ibm.fscc.loginservice.dto.AuthResponseDto;
import com.ibm.fscc.loginservice.dto.LoginDto;

import javax.naming.AuthenticationException;

public interface LoginService {
    
    /**
     * Get login details by email
     * 
     * @param email The email to look up
     * @return LoginDto with the user's login details, or null if not found
     */
    LoginDto getLogin(String email);
    
    /**
     * Authenticate a user with email and password
     * 
     * @param email The user's email
     * @param password The user's password
     * @return AuthResponseDto with authentication details and JWT token
     * @throws AuthenticationException If authentication fails
     */
    AuthResponseDto authenticateUser(String email, String password) throws AuthenticationException;
    
    /**
     * Request a password reset for the given email
     * 
     * @param email The email to reset password for
     * @return The generated reset token, or null if email not found
     */
    String requestPasswordReset(String email);
    
    /**
     * Update a user's password using a reset token
     * 
     * @param token The reset token
     * @param newPassword The new password
     */
    void updatePasswordWithResetToken(String token, String newPassword);
}