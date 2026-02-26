package com.ibm.fscc.registrationservice.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import com.ibm.fscc.common.exception.ApiError;
import com.ibm.fscc.kafka.dto.EmployeeEventDto;
import com.ibm.fscc.registrationservice.config.KafkaMongoSyncHealthIndicator;
import com.ibm.fscc.registrationservice.model.RegistrationEntity;
import com.ibm.fscc.registrationservice.repository.RegistrationRepository;
import com.ibm.fscc.registrationservice.service.impl.ProducerServiceImpl;

/**
 * Tests for exception handling in the registration service
 */
public class ExceptionHandlingTest {

    @Mock
    private RegistrationRepository kafkaRepository;

    @Mock
    private KafkaTemplate<String, EmployeeEventDto> kafkaTemplate;

    @Mock
    private KafkaMongoSyncHealthIndicator healthIndicator;

    @Mock
    private CompletableFuture<Object> future;

    @InjectMocks
    private ProducerServiceImpl producerService;

    @InjectMocks
    private RegistrationExceptionHandler exceptionHandler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testKafkaPublishExceptionHandling() {
        // Create a KafkaPublishException
        KafkaPublishException exception = new KafkaPublishException(
                "Failed to publish message",
                new RuntimeException("Connection refused"));

        // Test the exception handler
        ResponseEntity<ApiError> response = exceptionHandler.handleKafkaPublishException(exception);

        // Verify the response
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("KAFKA_PUBLISH_ERROR", response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Failed to publish message"));
        assertNotNull(response.getBody().getFieldErrors());
        assertTrue(response.getBody().getFieldErrors().containsKey("cause"));
        assertEquals("Connection refused", response.getBody().getFieldErrors().get("cause"));
    }

    @Test
    public void testSyncExceptionHandling() {
        // Create a SyncException
        SyncException exception = new SyncException(
                "Synchronization error between Kafka and MongoDB");

        // Test the exception handler
        ResponseEntity<ApiError> response = exceptionHandler.handleSyncException(exception);

        // Verify the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("SYNC_ERROR", response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Synchronization error"));
    }

    @Test
    public void testDataAccessExceptionHandling() {
        // Create a DataAccessException
        DataAccessException exception = new DataAccessException("Database error") {
        };

        // Test the exception handler
        ResponseEntity<ApiError> response = exceptionHandler.handleDataAccessException(exception);

        // Verify the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("DATABASE_ERROR", response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Database operation failed"));
    }

    @Test
    public void testHealthIndicatorThrowsSyncException() {
        // Configure the health indicator to throw an exception
        doThrow(new SyncException("Critical sync error"))
                .when(healthIndicator)
                .recordSyncError(anyString(), any(Throwable.class));

        // Create test data
        EmployeeEventDto event = new EmployeeEventDto();
        event.setEmail("test@example.com");
        Exception testException = new RuntimeException("Test exception");

        // Verify that the exception is thrown
        assertThrows(SyncException.class, () -> {
            healthIndicator.recordSyncError("Test error", testException);
        });
    }

    @Test
    public void testProducerServiceHandlesKafkaException() throws Exception {
        // Setup test data
        EmployeeEventDto event = new EmployeeEventDto();
        event.setEmail("test@example.com");

        // Mock the KafkaTemplate to throw an exception
        when(kafkaTemplate.send(anyString(), anyString(), any(EmployeeEventDto.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka error")));

        // Verify that the exception is handled and rethrown as KafkaPublishException
        assertThrows(KafkaPublishException.class, () -> {
            producerService.sendRegistrationEvent("test-topic", event);
        });

        // Verify that the health indicator was called
        verify(healthIndicator).recordKafkaError();
    }
}
