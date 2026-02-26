package com.ibm.fscc.loginservice.services;

import javax.naming.AuthenticationException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ibm.fscc.common.exception.ResourceNotFoundException;
import com.ibm.fscc.kafka.dto.PasswordResetEventDto;
import com.ibm.fscc.loginservice.common.KafkaTopics;
import com.ibm.fscc.loginservice.dto.AuthResponseDto;
import com.ibm.fscc.loginservice.dto.LoginDto;
import com.ibm.fscc.loginservice.exception.InvalidPasswordException;
import com.ibm.fscc.loginservice.exception.InvalidResetTokenException;
import com.ibm.fscc.loginservice.kafka.KafkaProducerService;
import com.ibm.fscc.loginservice.model.LoginEntity;
import com.ibm.fscc.loginservice.repository.LoginRepository;
import com.ibm.fscc.loginservice.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class LoginServiceImpl implements LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Value("${password.reset.base-url}")
    private String passwordResetBaseUrl;
    
    @Value("${app.frontend-url:http://fscc.local}")
    private String frontendUrl;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public LoginDto getLogin(String email) {
        LoginEntity loginEntity = loginRepository.findByEmail(email);

        if (loginEntity == null) {
            return null;
        }

        LoginDto loginDto = new LoginDto();
        BeanUtils.copyProperties(loginEntity, loginDto);

        return loginDto;
    }

    @Override
    public AuthResponseDto authenticateUser(String email, String password) throws AuthenticationException {
        LoginDto storedUserDetails = getLogin(email);

        // Check if user exists
        if (storedUserDetails == null) {
            throw new ResourceNotFoundException("User", "email", email);
        }

        // Password validation using BCrypt
        if (!passwordEncoder.matches(password, storedUserDetails.getPassword())) {
            throw new InvalidPasswordException("Password is incorrect");
        }

        // Generate JWT token - handle null ID by using email as ID if needed
        String token = jwtUtil.generateAuthToken(
                storedUserDetails.getEmail(),
                storedUserDetails.getUserId() != null ? storedUserDetails.getUserId() : storedUserDetails.getEmail(),
                storedUserDetails.getRole() != null ? storedUserDetails.getRole() : "USER");

        // Return authentication response with token
        return new AuthResponseDto(
                storedUserDetails.getId(),
                storedUserDetails.getEmail(),
                storedUserDetails.getRole(),
                storedUserDetails.isPasswordSet(),
                token);
    }

    // Can be used for resetting or first-time setting of password
    @Override
    public String requestPasswordReset(String email) {
        LoginEntity loginEntity = loginRepository.findByEmail(email);

        if (loginEntity == null) {
            // For security, don't reveal whether the email exists
            return null;
        }

        // Generate a token
        String token = jwtUtil.generatePasswordResetToken(email, 1800); // 30 minutes

        // Store token & expiry in the LoginEntity
        loginEntity.setResetToken(token);
        loginEntity.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        loginRepository.save(loginEntity);

        // Info log
        logger.debug("Generated password reset token.");
        // Log for debugging
        logger.debug("Generated password reset token for email: {}", email);
        
        // Send password reset event to Kafka for existing users (password already set)
        if (loginEntity.isPasswordSet()) {
            String resetUrl = frontendUrl + "/set-password?token=" + token;
            PasswordResetEventDto resetEvent = new PasswordResetEventDto(email, token, resetUrl);
            
            kafkaProducerService.sendPasswordResetEvent(
                    KafkaTopics.PASSWORD_RESET_REQUESTED.topicName(),
                    resetEvent
            );
            
            logger.info("Password reset event sent for user: {}", email);
        }
        
        return token;
    }

    @Override
    public void updatePasswordWithResetToken(String token, String newPassword) {
        Claims claims;
        try {
            claims = jwtUtil.validateToken(token);
        } catch (JwtException ex) {
            throw new InvalidResetTokenException("Reset token is invalid or expired");
        }

        // Ensure token is for password reset
        if (!"PASSWORD_RESET".equals(claims.get("purpose"))) {
            throw new InvalidResetTokenException("Invalid token purpose");
        }

        String email = claims.getSubject();
        LoginEntity loginEntity = loginRepository.findByEmail(email);
        if (loginEntity == null) {
            throw new ResourceNotFoundException("User", "email", email);
        }

        // Update password
        loginEntity.setPassword(passwordEncoder.encode(newPassword));
        loginEntity.setPasswordSet(true);
        loginEntity.setResetToken(null);
        loginEntity.setResetTokenExpiry(null);
        loginRepository.save(loginEntity);
    }
}
