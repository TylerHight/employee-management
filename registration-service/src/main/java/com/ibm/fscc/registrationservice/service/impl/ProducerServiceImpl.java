package com.ibm.fscc.registrationservice.service.impl;

import com.ibm.fscc.kafka.dto.EmployeeEventDto;
import com.ibm.fscc.registrationservice.common.KafkaTopics;
import com.ibm.fscc.registrationservice.service.ProducerService;
import com.ibm.fscc.registrationservice.config.KafkaMongoSyncHealthIndicator;
import com.ibm.fscc.registrationservice.exception.KafkaPublishException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class ProducerServiceImpl implements ProducerService {

    private static final Logger log = LoggerFactory.getLogger(ProducerServiceImpl.class);

    private final KafkaTemplate<String, EmployeeEventDto> kafkaTemplate;
    private final KafkaMongoSyncHealthIndicator healthIndicator;
    
    @Value("${kafka.producer.timeout:5000}")
    private long producerTimeoutMs = 5000; // Default 5 seconds
    
    @Value("${kafka.producer.retry.max-attempts:3}")
    private int maxRetryAttempts = 3;

    @Autowired
    public ProducerServiceImpl(KafkaTemplate<String, EmployeeEventDto> kafkaTemplate,
                              KafkaMongoSyncHealthIndicator healthIndicator) {
        this.kafkaTemplate = kafkaTemplate;
        this.healthIndicator = healthIndicator;
    }

    @Override
    @Retryable(
        value = {KafkaPublishException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Transactional
    public void sendRegistrationEvent(String topic, EmployeeEventDto event) {
        log.info("Producing EmployeeEventDto to topic {}: {}", topic, event);
        log.debug("[PRODUCER] Kafka Event - Topic: {}, Email: {}, UUID: {}", topic, event.getEmail(), event.getUserId());
        
        try {
            CompletableFuture<SendResult<String, EmployeeEventDto>> future =
                kafkaTemplate.send(topic, event.getEmail(), event);
            
            // Wait for the result with a timeout
            SendResult<String, EmployeeEventDto> result =
                future.get(producerTimeoutMs, TimeUnit.MILLISECONDS);
            
            log.info("Event sent successfully for email: {} to partition {} with offset {}",
                event.getEmail(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
                
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Failed to send event for email: {}", event.getEmail(), e);
            // Record error in health indicator
            healthIndicator.recordKafkaError();
            // Throw custom exception to trigger retry
            throw new KafkaPublishException("Failed to publish message to Kafka: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void sendToDLT(String originalTopic, EmployeeEventDto event, Exception exception) {
        String dltTopic = KafkaTopics.EMPLOYEE_EVENTS_DLT.topicName();
        log.warn("Sending failed message to Dead Letter Topic {}: {}", dltTopic, event);
        
        try {
            kafkaTemplate.send(dltTopic, event.getEmail(), event).get();
            log.info("Message sent to DLT for email: {}", event.getEmail());
            // Record sync error since the original message couldn't be processed
            healthIndicator.recordSyncError(
                "Original message couldn't be processed and was sent to DLT: " + event.getEmail(),
                exception
            );
        } catch (Exception e) {
            log.error("Failed to send message to DLT for email: {}", event.getEmail(), e);
            healthIndicator.recordKafkaError();
        }
    }
}
