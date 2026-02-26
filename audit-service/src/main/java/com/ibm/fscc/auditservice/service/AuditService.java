package com.ibm.fscc.auditservice.service;

import com.ibm.fscc.auditservice.dto.AuditEventDto;
import com.ibm.fscc.auditservice.dto.AuditQueryRequest;
import com.ibm.fscc.auditservice.model.AuditEvent;
import org.springframework.data.domain.Page;

public interface AuditService {

    /**
     * Save an audit event
     */
    AuditEvent saveAuditEvent(AuditEventDto auditEventDto);

    /**
     * Query audit events with filters
     */
    Page<AuditEvent> queryAuditEvents(AuditQueryRequest request);

    /**
     * Get audit event by ID
     */
    AuditEvent getAuditEventById(Long id);

    /**
     * Get audit event by event ID
     */
    AuditEvent getAuditEventByEventId(String eventId);

    /**
     * Delete expired audit events based on retention policy
     */
    int deleteExpiredAuditEvents();

    /**
     * Get audit statistics for a user
     */
    long getUserAuditCount(String userId, int days);

    /**
     * Get audit statistics for an event type
     */
    long getEventTypeCount(String eventType, int days);
}
