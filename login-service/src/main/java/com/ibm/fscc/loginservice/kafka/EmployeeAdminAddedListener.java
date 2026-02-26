package com.ibm.fscc.loginservice.kafka;

import com.ibm.fscc.kafka.dto.EmployeeEventDto;
import com.ibm.fscc.loginservice.model.LoginEntity;
import com.ibm.fscc.loginservice.repository.LoginRepository;
import com.ibm.fscc.loginservice.services.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmployeeAdminAddedListener {

    private static final Logger log = LoggerFactory.getLogger(EmployeeAdminAddedListener.class);

    private final LoginRepository loginRepository;
    private final LoginService loginService;

    public EmployeeAdminAddedListener(LoginRepository loginRepository, LoginService loginService) {
        this.loginRepository = loginRepository;
        this.loginService = loginService;
    }

    @KafkaListener(topics = "employee-admin-added", groupId = "${spring.kafka.consumer.group-id}")
    public void handleEmployeeAdminAdded(EmployeeEventDto event) {
        log.info("Received admin-added employee event in login-service with userId: {}, role: {}",
                event.getUserId(), event.getRole());

        // Idempotency check — avoid duplicate login records
        if (loginRepository.findByEmail(event.getEmail()) != null) {
            log.warn("Login already exists for email {}, skipping creation", event.getEmail());
            return;
        }

        // Create new login record
        LoginEntity login = new LoginEntity();
        login.setUserId(event.getUserId());
        login.setEmail(event.getEmail());
        login.setRole(event.getRole() != null ? event.getRole() : "USER"); // Use role from event or default to USER
        login.setPassword(""); // no password yet
        login.setPasswordSet(false);

        loginRepository.save(login);
        
        // Generate password reset token for the new employee
        loginService.requestPasswordReset(event.getEmail());
        log.info("Password reset requested for admin-added employee: {}", event.getEmail());
    }
}