package com.ibm.fscc.loginservice.kafka;

import com.ibm.fscc.kafka.dto.PasswordResetEventDto;
import com.ibm.fscc.kafka.dto.PasswordSetupEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for producing Kafka messages from the login service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Sends a password setup event to the specified Kafka topic.
     *
     * @param topic The Kafka topic to send the event to
     * @param event The password setup event data
     * @return CompletableFuture with the result of the send operation
     */
    public CompletableFuture<SendResult<String, Object>> sendPasswordSetupEvent(String topic,
            PasswordSetupEventDto event) {
        log.info("Sending password setup event to topic {} for user: {}, email: {}",
                topic, event.getUserId(), event.getEmail());

        return kafkaTemplate.send(topic, event.getEmail(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Password setup event sent successfully to topic: {}, partition: {}, offset: {}",
                                topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send password setup event to topic: {}, error: {}",
                                topic, ex.getMessage(), ex);
                    }
                });
    }

    /**
     * Sends a password reset event to the specified Kafka topic.
     *
     * @param topic The Kafka topic to send the event to
     * @param event The password reset event data
     * @return CompletableFuture with the result of the send operation
     */
    public CompletableFuture<SendResult<String, Object>> sendPasswordResetEvent(String topic,
            PasswordResetEventDto event) {
        log.info("Sending password reset event to topic {} for email: {}",
                topic, event.getEmail());

        return kafkaTemplate.send(topic, event.getEmail(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Password reset event sent successfully to topic: {}, partition: {}, offset: {}",
                                topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send password reset event to topic: {}, error: {}",
                                topic, ex.getMessage(), ex);
                    }
                });
    }
}
