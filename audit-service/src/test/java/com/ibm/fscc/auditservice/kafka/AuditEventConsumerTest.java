package com.ibm.fscc.auditservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.fscc.auditservice.dto.AuditEventDto;
import com.ibm.fscc.auditservice.model.AuditEvent;
import com.ibm.fscc.auditservice.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditEventConsumerTest {

    @Mock
    private AuditService auditService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuditEventConsumer auditEventConsumer;

    private String testMessage;
    private AuditEventDto testAuditEventDto;
    private AuditEvent testAuditEvent;

    @BeforeEach
    void setUp() {
        testAuditEventDto = AuditEventDto.builder()
                .eventType("USER_LOGIN")
                .userId("user123")
                .userEmail("user@example.com")
                .serviceName("login-service")
                .action("LOGIN")
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();

        testAuditEvent = AuditEvent.builder()
                .id(1L)
                .eventId("event-123")
                .eventType("USER_LOGIN")
                .userId("user123")
                .build();

        testMessage = "{\"eventType\":\"USER_LOGIN\",\"userId\":\"user123\"}";
    }

    @Test
    void testConsumeAuditEvent_Success() throws Exception {
        // Arrange
        when(objectMapper.readValue(testMessage, AuditEventDto.class)).thenReturn(testAuditEventDto);
        when(auditService.saveAuditEvent(any(AuditEventDto.class))).thenReturn(testAuditEvent);

        // Act
        auditEventConsumer.consumeAuditEvent(testMessage, "audit.events", 0, 0L);

        // Assert
        verify(objectMapper, times(1)).readValue(testMessage, AuditEventDto.class);
        verify(auditService, times(1)).saveAuditEvent(any(AuditEventDto.class));
    }

    @Test
    void testConsumeAuditEvent_ParseError() throws Exception {
        // Arrange
        when(objectMapper.readValue(testMessage, AuditEventDto.class))
                .thenThrow(new RuntimeException("Parse error"));

        // Act
        auditEventConsumer.consumeAuditEvent(testMessage, "audit.events", 0, 0L);

        // Assert
        verify(objectMapper, times(1)).readValue(testMessage, AuditEventDto.class);
        verify(auditService, never()).saveAuditEvent(any(AuditEventDto.class));
    }

    @Test
    void testConsumeApiGatewayEvent_Success() throws Exception {
        // Arrange
        when(objectMapper.readValue(testMessage, AuditEventDto.class)).thenReturn(testAuditEventDto);
        when(auditService.saveAuditEvent(any(AuditEventDto.class))).thenReturn(testAuditEvent);

        // Act
        auditEventConsumer.consumeApiGatewayEvent(testMessage, "audit.api-gateway");

        // Assert
        verify(objectMapper, times(1)).readValue(testMessage, AuditEventDto.class);
        verify(auditService, times(1)).saveAuditEvent(any(AuditEventDto.class));
    }

    @Test
    void testConsumeAuthenticationEvent_Success() throws Exception {
        // Arrange
        when(objectMapper.readValue(testMessage, AuditEventDto.class)).thenReturn(testAuditEventDto);
        when(auditService.saveAuditEvent(any(AuditEventDto.class))).thenReturn(testAuditEvent);

        // Act
        auditEventConsumer.consumeAuthenticationEvent(testMessage, "audit.authentication");

        // Assert
        verify(objectMapper, times(1)).readValue(testMessage, AuditEventDto.class);
        verify(auditService, times(1)).saveAuditEvent(any(AuditEventDto.class));
    }
}
