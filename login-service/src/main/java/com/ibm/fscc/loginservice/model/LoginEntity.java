package com.ibm.fscc.loginservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class LoginEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 36)
    private String userId;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // encrypted

    @Column(nullable = false, length = 50)
    private String role = "USER"; // default role

    // For password reset
    @Column(name = "reset_token", unique = true)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    // For onboarding flow (false if the password has not been set yet)
    @Column(name = "password_set", nullable = false)
    private boolean passwordSet = true;
}
