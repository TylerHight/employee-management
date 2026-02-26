package com.ibm.fscc.loginservice.kafka;

import com.ibm.fscc.kafka.dto.PasswordResetEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetProducer {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetProducer.class);
    private static final String TOPIC = "password-reset";

    private final KafkaTemplate<String, PasswordResetEventDto> kafkaTemplate;

    public PasswordResetProducer(KafkaTemplate<String, PasswordResetEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPasswordResetEvent(String email, String resetToken, String resetLink) {
        PasswordResetEventDto event = new PasswordResetEventDto(email, resetToken, resetLink);

        logger.info("Sending password reset event to Kafka for email: {}", email);
        kafkaTemplate.send(TOPIC, email, event);
    }
}
