package com.ibm.fscc.loginservice.kafka;

import com.ibm.fscc.kafka.dto.EmployeeDeletedEvent;
import com.ibm.fscc.loginservice.model.LoginEntity;
import com.ibm.fscc.loginservice.repository.LoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmployeeDeletedListener {

    private static final Logger log = LoggerFactory.getLogger(EmployeeDeletedListener.class);

    private final LoginRepository loginRepository;

    public EmployeeDeletedListener(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @KafkaListener(topics = "employee-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void handleEmployeeDeleted(EmployeeDeletedEvent event) {
        log.info("Received employee-deleted event in login-service with userId: {}, email: {}",
                event.getUserId(), event.getEmail());

        // Find login by userId
        LoginEntity login = loginRepository.findByUserId(event.getUserId());
        if (login == null) {
            // Try finding by email as fallback
            login = loginRepository.findByEmail(event.getEmail());
            if (login == null) {
                log.warn("No login found for userId: {} or email: {}, nothing to delete", 
                        event.getUserId(), event.getEmail());
                return;
            }
        }

        // Delete the login record
        loginRepository.delete(login);
        log.info("Deleted login record for userId: {}, email: {}", event.getUserId(), event.getEmail());
    }
}