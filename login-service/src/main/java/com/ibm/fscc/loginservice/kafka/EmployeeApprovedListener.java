package com.ibm.fscc.loginservice.kafka;

import com.ibm.fscc.kafka.dto.EmployeeEventDto;
import com.ibm.fscc.loginservice.common.KafkaTopics;
import com.ibm.fscc.kafka.dto.PasswordSetupEventDto;
import com.ibm.fscc.loginservice.model.LoginEntity;
import com.ibm.fscc.loginservice.repository.LoginRepository;
import com.ibm.fscc.loginservice.services.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeApprovedListener {

    private final LoginRepository loginRepository;
    private final KafkaProducerService kafkaProducerService;
    private final LoginService loginService;
    
    @Value("${app.frontend-url:http://fscc.local}")
    private String frontendUrl;

    public EmployeeApprovedListener(
            LoginRepository loginRepository,
            KafkaProducerService kafkaProducerService,
            LoginService loginService) {
        this.loginRepository = loginRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.loginService = loginService;
    }

    @KafkaListener(topics = "#{T(com.ibm.fscc.loginservice.common.KafkaTopics).EMPLOYEE_APPROVED.topicName()}",
                  groupId = "${spring.kafka.consumer.group-id}")
    public void handleEmployeeApproved(EmployeeEventDto event) {
        log.info("Received EmployeeEventDto in login-service with userId: {}", event.getUserId());

        // Idempotency check — avoid duplicate login records
        if (loginRepository.findByEmail(event.getEmail()) != null) {
            log.warn("Login already exists for email {}, skipping creation", event.getEmail());
            return;
        }

        // Create new login record
        LoginEntity login = new LoginEntity();
        login.setUserId(event.getUserId());
        login.setEmail(event.getEmail());
        login.setRole("USER");
        login.setPassword(""); // no password yet
        login.setPasswordSet(false);

        loginRepository.save(login);
        
        // Request password reset which will generate a token
        String setupToken = loginService.requestPasswordReset(event.getEmail());
        
        // Create the setup URL with the actual token
        String setupUrl = frontendUrl + "/set-password?token=" + setupToken;
        
        // Create and send password setup event to notification service
        PasswordSetupEventDto setupEvent = new PasswordSetupEventDto(
                event.getUserId(),
                event.getFirstName(),
                event.getLastName(),
                event.getEmail(),
                setupToken,
                setupUrl
        );
        
        kafkaProducerService.sendPasswordSetupEvent(
                KafkaTopics.PASSWORD_SETUP_REQUESTED.topicName(),
                setupEvent
        );
        
        log.info("Password setup event sent for user: {}, email: {}", event.getUserId(), event.getEmail());
    }
}
