package com.ibm.fscc.registrationservice.service;

import com.ibm.fscc.registrationservice.common.KafkaTopics;
import com.ibm.fscc.registrationservice.config.KafkaMongoSyncHealthIndicator;
import com.ibm.fscc.kafka.dto.EmployeeEventDto;
import com.ibm.fscc.registrationservice.dto.RegistrationResponseDto;
import com.ibm.fscc.registrationservice.dto.RegistrationStatus;
import com.ibm.fscc.registrationservice.exception.KafkaPublishException;
import com.ibm.fscc.registrationservice.model.RegistrationEntity;
import com.ibm.fscc.registrationservice.repository.RegistrationRepository;
import com.ibm.fscc.registrationservice.service.impl.RegistrationServiceImpl;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Kafka–MongoDB synchronization and error handling.
 */
public class KafkaMongoSyncTest {

    private RegistrationRepository kafkaRepository;
    private ProducerService producerService;
    private KafkaMongoSyncHealthIndicator healthIndicator;
    private RegistrationServiceImpl registrationService;

    // Mocks for Kafka send result
    private CompletableFuture<SendResult<String, EmployeeEventDto>> future;
    private SendResult<String, EmployeeEventDto> sendResult;

    @BeforeEach
    void setup() {
        kafkaRepository = mock(RegistrationRepository.class);
        producerService = mock(ProducerService.class);
        healthIndicator = mock(KafkaMongoSyncHealthIndicator.class);

        MockitoAnnotations.openMocks(this);
        registrationService = new RegistrationServiceImpl(kafkaRepository, producerService, healthIndicator);

        // Set up dummy SendResult with valid RecordMetadata
        ProducerRecord<String, EmployeeEventDto> record = new ProducerRecord<>(
                KafkaTopics.EMPLOYEE_APPROVED.topicName(), "test@example.com", new EmployeeEventDto());
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition(KafkaTopics.EMPLOYEE_APPROVED.topicName(), 0),
                0, 0, System.currentTimeMillis(), Long.valueOf(0), 0, 0);
        sendResult = new SendResult<>(record, metadata);
        future = mock(CompletableFuture.class);
    }

    @Test
    public void testSuccessfulApprovalSync() throws Exception {
        // Setup test data
        String email = "test@example.com";
        String userId = UUID.randomUUID().toString();

        RegistrationEntity entity = new RegistrationEntity();
        entity.setId("1");
        entity.setUserId(userId);
        entity.setEmail(email);
        entity.setFirstName("Test");
        entity.setLastName("User");
        entity.setStatus(RegistrationStatus.PENDING.name());
        entity.setStatusDate(LocalDateTime.now());

        // Mock repository behavior
        when(kafkaRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(kafkaRepository.save(any(RegistrationEntity.class))).thenReturn(entity);

        // Mock producer behavior for successful send
        when(future.get(anyLong(), any())).thenReturn(sendResult);

        // Execute the approval
        RegistrationResponseDto response = registrationService.approveRegistration(email);

        // Verify MongoDB was updated
        verify(kafkaRepository)
                .save(argThat(savedEntity -> savedEntity.getStatus().equals(RegistrationStatus.APPROVED.name())));

        // Verify Kafka message was sent
        verify(producerService).sendRegistrationEvent(
                eq(KafkaTopics.EMPLOYEE_APPROVED.topicName()),
                argThat(event -> event.getEmail().equals(email) &&
                        event.getUserId().equals(userId)));

        // Verify response
        assertEquals(RegistrationStatus.APPROVED, response.getStatus());
        assertEquals(email, response.getEmail());
    }

    @Test
    public void testKafkaFailureRollback() throws Exception {
        // Setup test data
        String email = "test@example.com";
        String userId = UUID.randomUUID().toString();

        RegistrationEntity entity = new RegistrationEntity();
        entity.setId("1");
        entity.setUserId(userId);
        entity.setEmail(email);
        entity.setFirstName("Test");
        entity.setLastName("User");
        entity.setStatus(RegistrationStatus.PENDING.name());
        entity.setStatusDate(LocalDateTime.now());

        // Mock repository behavior
        when(kafkaRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(kafkaRepository.save(any(RegistrationEntity.class))).thenReturn(entity);

        // Mock Kafka behavior for failed send
        doThrow(new KafkaPublishException("Kafka send failed"))
                .when(producerService)
                .sendRegistrationEvent(anyString(), any(EmployeeEventDto.class));
        when(future.get(anyLong(), any())).thenThrow(new KafkaPublishException("Kafka send failed"));

        // Execute and verify exception is thrown
        assertThrows(KafkaPublishException.class, () -> registrationService.approveRegistration(email));

        // Verify health indicator recorded sync error
        verify(healthIndicator).recordSyncError(
                contains("Kafka publish failed after MongoDB update"), any(KafkaPublishException.class));
    }

    @Test
    public void testMongoFailureHandling() {
        // Setup test data
        String email = "test@example.com";
        String userId = UUID.randomUUID().toString();

        RegistrationEntity entity = new RegistrationEntity();
        entity.setId("1");
        entity.setUserId(userId);
        entity.setEmail(email);
        entity.setFirstName("Test");
        entity.setLastName("User");
        entity.setStatus(RegistrationStatus.PENDING.name());
        entity.setStatusDate(LocalDateTime.now());

        // Mock repository behavior for failure
        when(kafkaRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(kafkaRepository.save(any(RegistrationEntity.class)))
                .thenThrow(new DataAccessException("MongoDB save failed") {
                });

        // Execute and verify exception is thrown
        assertThrows(DataAccessException.class, () -> registrationService.approveRegistration(email));

        // Verify health indicator was updated
        verify(healthIndicator).recordMongoError();

        // Verify producer was not called
        verify(producerService, never()).sendRegistrationEvent(anyString(), any(EmployeeEventDto.class));
    }

    @Test
    public void testIdempotentProcessing() {
        // This placeholder describes how an integration test would verify idempotency:
        // 1. A message is processed successfully.
        // 2. The same message is sent again.
        // 3. The second message is ignored.
        // Not implemented here because it requires integration testing with a running
        // Kafka broker.
    }
}
