package com.ibm.fscc.auditservice.service;

import com.ibm.fscc.auditservice.dto.AuditEventDto;
import com.ibm.fscc.auditservice.dto.AuditQueryRequest;
import com.ibm.fscc.auditservice.model.AuditEvent;
import com.ibm.fscc.auditservice.repository.AuditEventRepository;
import com.ibm.fscc.auditservice.service.impl.AuditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditEventRepository auditEventRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

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
                .ipAddress("192.168.1.1")
                .timestamp(LocalDateTime.now())
                .build();

        testAuditEvent = AuditEvent.builder()
                .id(1L)
                .eventId("event-123")
                .eventType("USER_LOGIN")
                .userId("user123")
                .userEmail("user@example.com")
                .serviceName("login-service")
                .action("LOGIN")
                .status("SUCCESS")
                .ipAddress("192.168.1.1")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void testSaveAuditEvent_Success() {
        // Arrange
        when(auditEventRepository.save(any(AuditEvent.class))).thenReturn(testAuditEvent);

        // Act
        AuditEvent result = auditService.saveAuditEvent(testAuditEventDto);

        // Assert
        assertNotNull(result);
        assertEquals("USER_LOGIN", result.getEventType());
        assertEquals("user123", result.getUserId());
        verify(auditEventRepository, times(1)).save(any(AuditEvent.class));
    }

    @Test
    void testQueryAuditEvents_Success() {
        // Arrange
        List<AuditEvent> events = Arrays.asList(testAuditEvent);
        Page<AuditEvent> page = new PageImpl<>(events);

        when(auditEventRepository.findByFilters(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        AuditQueryRequest request = AuditQueryRequest.builder()
                .eventType("USER_LOGIN")
                .page(0)
                .size(20)
                .build();

        // Act
        Page<AuditEvent> result = auditService.queryAuditEvents(request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("USER_LOGIN", result.getContent().get(0).getEventType());
        verify(auditEventRepository, times(1)).findByFilters(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void testGetAuditEventById_Success() {
        // Arrange
        when(auditEventRepository.findById(1L)).thenReturn(Optional.of(testAuditEvent));

        // Act
        AuditEvent result = auditService.getAuditEventById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("USER_LOGIN", result.getEventType());
        verify(auditEventRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAuditEventById_NotFound() {
        // Arrange
        when(auditEventRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> auditService.getAuditEventById(999L));
        verify(auditEventRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAuditEventByEventId_Success() {
        // Arrange
        when(auditEventRepository.findByEventId("event-123")).thenReturn(Optional.of(testAuditEvent));

        // Act
        AuditEvent result = auditService.getAuditEventByEventId("event-123");

        // Assert
        assertNotNull(result);
        assertEquals("event-123", result.getEventId());
        verify(auditEventRepository, times(1)).findByEventId("event-123");
    }

    @Test
    void testDeleteExpiredAuditEvents_Success() {
        // Arrange
        List<AuditEvent> expiredEvents = Arrays.asList(testAuditEvent);
        when(auditEventRepository.findByRetentionDateBefore(any(LocalDateTime.class)))
                .thenReturn(expiredEvents);
        doNothing().when(auditEventRepository).deleteAll(expiredEvents);

        // Act
        int deletedCount = auditService.deleteExpiredAuditEvents();

        // Assert
        assertEquals(1, deletedCount);
        verify(auditEventRepository, times(1)).findByRetentionDateBefore(any(LocalDateTime.class));
        verify(auditEventRepository, times(1)).deleteAll(expiredEvents);
    }

    @Test
    void testDeleteExpiredAuditEvents_NoExpiredEvents() {
        // Arrange
        when(auditEventRepository.findByRetentionDateBefore(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList());

        // Act
        int deletedCount = auditService.deleteExpiredAuditEvents();

        // Assert
        assertEquals(0, deletedCount);
        verify(auditEventRepository, times(1)).findByRetentionDateBefore(any(LocalDateTime.class));
        verify(auditEventRepository, never()).deleteAll(any());
    }

    @Test
    void testGetUserAuditCount_Success() {
        // Arrange
        when(auditEventRepository.countByUserIdSince(eq("user123"), any(LocalDateTime.class)))
                .thenReturn(10L);

        // Act
        long count = auditService.getUserAuditCount("user123", 30);

        // Assert
        assertEquals(10L, count);
        verify(auditEventRepository, times(1)).countByUserIdSince(eq("user123"), any(LocalDateTime.class));
    }

    @Test
    void testGetEventTypeCount_Success() {
        // Arrange
        when(auditEventRepository.countByEventTypeSince(eq("USER_LOGIN"), any(LocalDateTime.class)))
                .thenReturn(25L);

        // Act
        long count = auditService.getEventTypeCount("USER_LOGIN", 30);

        // Assert
        assertEquals(25L, count);
        verify(auditEventRepository, times(1)).countByEventTypeSince(eq("USER_LOGIN"), any(LocalDateTime.class));
    }
}
