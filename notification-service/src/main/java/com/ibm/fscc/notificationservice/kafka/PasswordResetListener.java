package com.ibm.fscc.notificationservice.kafka;

import com.ibm.fscc.kafka.dto.PasswordResetEventDto;
import com.ibm.fscc.notificationservice.dto.EmailResponse;
import com.ibm.fscc.notificationservice.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka listener for password reset events.
 * Sends password reset emails to users who request to reset their password.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetListener {

    private final EmailService emailService;

    /**
     * Handles password reset events from Kafka.
     * Sends an email with a password reset link to the user.
     *
     * @param event The password reset event data
     */
    @KafkaListener(topics = "#{T(com.ibm.fscc.notificationservice.common.KafkaTopics).PASSWORD_RESET_REQUESTED.topicName()}",
                  groupId = "${spring.kafka.consumer.group-id}")
    public void handlePasswordResetRequested(PasswordResetEventDto event) {
        log.info("Received password-reset-requested event for email: {}", event.getEmail());

        try {
            // Prepare template variables
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("resetUrl", event.getResetLink());

            // Send password reset email
            EmailResponse response = emailService.sendTemplateEmail(
                    event.getEmail(),
                    "Password Reset Request",
                    "password-reset-email",
                    templateModel);

            if (response.isSent()) {
                log.info("Password reset email sent successfully to: {}", event.getEmail());
            } else {
                log.error("Failed to send password reset email to: {}, error: {}",
                        event.getEmail(), response.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Error sending password reset email to {}: {}", event.getEmail(), e.getMessage(), e);
        }
    }
}