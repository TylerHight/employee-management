package com.ibm.fscc.notificationservice.kafka;

import com.ibm.fscc.kafka.dto.PasswordSetupEventDto;
import com.ibm.fscc.notificationservice.dto.EmailResponse;
import com.ibm.fscc.notificationservice.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka listener for password setup events.
 * Sends password setup emails to users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordSetupListener {

    private final EmailService emailService;

    /**
     * Handles password setup events from Kafka.
     * Sends an email with a password setup link to the user.
     *
     * @param event The password setup event data
     */
    @KafkaListener(topics = "#{T(com.ibm.fscc.notificationservice.common.KafkaTopics).PASSWORD_SETUP_REQUESTED.topicName()}",
                  groupId = "${spring.kafka.consumer.group-id}")
    public void handlePasswordSetupRequested(PasswordSetupEventDto event) {
        log.info("Received password-setup-requested event for user: {}, email: {}",
                event.getUserId(), event.getEmail());

        try {
            // Prepare template variables
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("name", event.getFirstName() + " " + event.getLastName());
            templateModel.put("loginUrl", event.getSetupUrl());

            // Send password setup email
            EmailResponse response = emailService.sendTemplateEmail(
                    event.getEmail(),
                    "Welcome - Set Up Your Password",
                    "password-setup-email",
                    templateModel);

            if (response.isSent()) {
                log.info("Password setup email sent successfully to: {}", event.getEmail());
            } else {
                log.error("Failed to send password setup email to: {}, error: {}",
                        event.getEmail(), response.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Error sending password setup email to {}: {}", event.getEmail(), e.getMessage(), e);
        }
    }
}
