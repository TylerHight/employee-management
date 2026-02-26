package com.ibm.fscc.loginservice.kafka;

import com.ibm.fscc.kafka.dto.EmployeeEmailChangedEvent;
import com.ibm.fscc.loginservice.model.LoginEntity;
import com.ibm.fscc.loginservice.repository.LoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmployeeEmailChangedListener {

    private static final Logger log = LoggerFactory.getLogger(EmployeeEmailChangedListener.class);

    private final LoginRepository loginRepository;

    public EmployeeEmailChangedListener(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @KafkaListener(topics = "employee-email-changed", groupId = "${spring.kafka.consumer.group-id}")
    public void handleEmployeeEmailChanged(EmployeeEmailChangedEvent event) {
        log.info("Received employee-email-changed event in login-service with userId: {}, oldEmail: {}, newEmail: {}",
                event.getUserId(), event.getOldEmail(), event.getNewEmail());

        // Find login by userId
        LoginEntity login = loginRepository.findByUserId(event.getUserId());
        if (login == null) {
            // Try finding by old email as fallback
            login = loginRepository.findByEmail(event.getOldEmail());
            if (login == null) {
                log.warn("No login found for userId: {} or email: {}, cannot update email", 
                        event.getUserId(), event.getOldEmail());
                return;
            }
        }

        // Update the email
        login.setEmail(event.getNewEmail());
        loginRepository.save(login);
        log.info("Updated login email for userId: {} from {} to {}", 
                event.getUserId(), event.getOldEmail(), event.getNewEmail());
    }
}