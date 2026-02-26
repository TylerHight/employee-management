package com.ibm.fscc.registrationservice.service.impl;

import com.ibm.fscc.kafka.dto.EmployeeEventDto;
import com.ibm.fscc.registrationservice.common.KafkaTopics;
import com.ibm.fscc.registrationservice.dto.RegistrationStatus;
import com.ibm.fscc.registrationservice.config.KafkaMongoSyncHealthIndicator;
import com.ibm.fscc.registrationservice.repository.RegistrationRepository;
import com.ibm.fscc.registrationservice.service.ConsumerService;
import com.ibm.fscc.registrationservice.service.ProducerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ConsumerServiceImpl implements ConsumerService {

    private static final Logger log = LoggerFactory.getLogger(ConsumerServiceImpl.class);
    private final RegistrationRepository kafkaRepository;
    private final WebClient.Builder webClientBuilder;
    private final ProducerService producerService;
    private final KafkaMongoSyncHealthIndicator healthIndicator;
    
    // In-memory cache for processed message IDs to ensure idempotence
    // In a production environment, consider using Redis or another distributed cache
    private final ConcurrentMap<String, LocalDateTime> processedMessages = new ConcurrentHashMap<>();
    
    @Autowired
    public ConsumerServiceImpl(RegistrationRepository kafkaRepository, WebClient.Builder webClientBuilder,
                              ProducerService producerService, KafkaMongoSyncHealthIndicator healthIndicator) {
        this.kafkaRepository = kafkaRepository;
        this.webClientBuilder = webClientBuilder;
        this.producerService = producerService;
        this.healthIndicator = healthIndicator;
    }

    @Override
    @KafkaListener(topics = "#{T(com.ibm.fscc.registrationservice.common.KafkaTopics).EMPLOYEE_APPROVED.topicName()}",
                  groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    @Retryable(
        value = {DataAccessException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void consumeEmployeeEvent(EmployeeEventDto event, Acknowledgment acknowledgment) {
        String messageId = event.getUserId() + "-" + event.getEmail();
        
        try {
            log.info("Received employee event: {}", event);
            log.debug("Received Kafka Event UUID: {}", event.getUserId());
            
            // Check if this message has already been processed (idempotence check)
            if (processedMessages.containsKey(messageId)) {
                log.info("Message already processed, skipping: {}", messageId);
                acknowledgment.acknowledge();
                return;
            }
            
            kafkaRepository.findByEmail(event.getEmail()).ifPresentOrElse(entity -> {
                // Check if the entity is already in the target state
                if (RegistrationStatus.APPROVED.name().equals(entity.getStatus())) {
                    log.info("Registration already in APPROVED state for email: {}", event.getEmail());
                    acknowledgment.acknowledge();
                    return;
                }
                
                log.debug("DB Lookup UUID: {}, Email: {}", entity.getUserId(), entity.getEmail());
                entity.setStatus(RegistrationStatus.APPROVED.name());
                entity.setStatusDate(LocalDateTime.now());
                
                try {
                    kafkaRepository.save(entity);
                    log.info("Updated registration to APPROVED for email: {}", event.getEmail());
                    
                    // Add to processed messages cache with timestamp
                    processedMessages.put(messageId, LocalDateTime.now());
                    
                    // Acknowledge the message
                    acknowledgment.acknowledge();
                } catch (DataAccessException e) {
                    log.error("Failed to update registration status in MongoDB", e);
                    // Record MongoDB error in health indicator
                    healthIndicator.recordMongoError();
                    // Don't acknowledge - will be retried
                    throw e;
                }
            }, () -> {
                log.warn("No registration found with email: {}", event.getEmail());
                // Record sync error since MongoDB and Kafka are out of sync
                RuntimeException syncException = new RuntimeException("No registration found with email: " + event.getEmail());
                healthIndicator.recordSyncError("Kafka message received for non-existent registration", syncException);
                // Send to dead letter topic since we can't process this message
                producerService.sendToDLT(KafkaTopics.EMPLOYEE_APPROVED.topicName(), event, syncException);
                acknowledgment.acknowledge(); // Acknowledge to prevent reprocessing
            });
            
        } catch (Exception e) {
            log.error("Error processing message: {}", messageId, e);
            
            // After max retries, send to DLT
            if (e instanceof DataAccessException && !isRetryable(e)) {
                log.warn("Max retries reached or non-retryable error, sending to DLT");
                healthIndicator.recordSyncError(
                    "Failed to process message after max retries: " + messageId,
                    e
                );
                producerService.sendToDLT(KafkaTopics.EMPLOYEE_APPROVED.topicName(), event, e);
                acknowledgment.acknowledge(); // Acknowledge to prevent further retries
            }
            // For retryable exceptions, don't acknowledge to allow retry
        }
    }

    private boolean isRetryable(Exception e) {
        // Implement logic to determine if an exception is retryable
        // For example, network issues might be retryable, but data validation errors are not
        return !(e.getMessage() != null &&
               (e.getMessage().contains("duplicate key") ||
                e.getMessage().contains("validation failed")));
    }

    private void sendEmployeeCreationRequest(EmployeeEventDto employeeDto) {
        log.info("Sending approved employee to employee-service: {}", employeeDto);
        log.debug("[FORWARD] Sending to employee-service - Email: {}, UUID: {}", employeeDto.getEmail(),
                employeeDto.getUserId());
        // This should be made more secure, like routing through the gateway
        webClientBuilder.build()
                .post()
                .uri("http://employee-service/api/employees/approved")
                .header("X-User-Id", "system") // some system/service account ID
                .header("X-User-Role", "ADMIN") // role with permission to create employees
                .bodyValue(employeeDto)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Employee-service response: {}", response))
                .doOnError(error -> log.error("Failed to create employee in employee-service", error))
                .onErrorResume(e -> Mono.empty())
                .subscribe();
    }
    
    // Cleanup method to remove old processed message IDs (could be scheduled)
    public void cleanupProcessedMessages() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        processedMessages.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
        log.info("Cleaned up processed message cache, remaining entries: {}", processedMessages.size());
    }
}
