package com.ibm.fscc.auditservice.service.impl;

import com.ibm.fscc.auditservice.dto.AuditEventDto;
import com.ibm.fscc.auditservice.dto.AuditQueryRequest;
import com.ibm.fscc.auditservice.model.AuditEvent;
import com.ibm.fscc.auditservice.repository.AuditEventRepository;
import com.ibm.fscc.auditservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditEventRepository auditEventRepository;

    @Override
    @Transactional
    public AuditEvent saveAuditEvent(AuditEventDto dto) {
        log.debug("Saving audit event: {}", dto.getEventType());

        AuditEvent auditEvent = AuditEvent.builder()
                .eventId(dto.getEventId() != null ? dto.getEventId() : UUID.randomUUID().toString())
                .eventType(dto.getEventType())
                .aggregateType(dto.getAggregateType())
                .aggregateId(dto.getAggregateId())
                .timestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now())
                .userId(dto.getUserId())
                .userEmail(dto.getUserEmail())
                .ipAddress(dto.getIpAddress())
                .userAgent(dto.getUserAgent())
                .serviceName(dto.getServiceName())
                .serviceVersion(dto.getServiceVersion())
                .correlationId(dto.getCorrelationId())
                .sessionId(dto.getSessionId())
                .action(dto.getAction())
                .status(dto.getStatus())
                .beforeState(dto.getBeforeState())
                .afterState(dto.getAfterState())
                .changes(dto.getChanges())
                .errorMessage(dto.getErrorMessage())
                .metadata(dto.getMetadata())
                .resourceType(dto.getResourceType())
                .resourceId(dto.getResourceId())
                .httpMethod(dto.getHttpMethod())
                .requestPath(dto.getRequestPath())
                .responseCode(dto.getResponseCode())
                .responseTimeMs(dto.getResponseTimeMs())
                .complianceTag(dto.getComplianceTag())
                .retentionDate(calculateRetentionDate(dto.getComplianceTag()))
                .build();

        AuditEvent saved = auditEventRepository.save(auditEvent);
        log.info("Audit event saved: {} - {}", saved.getEventType(), saved.getEventId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEvent> queryAuditEvents(AuditQueryRequest request) {
        log.debug("Querying audit events with filters: {}", request);

        Pageable pageable = createPageable(request);

        return auditEventRepository.findByFilters(
                request.getEventType(),
                request.getUserId(),
                request.getServiceName(),
                request.getAggregateId(),
                request.getAction(),
                request.getStatus(),
                request.getResourceType(),
                request.getResourceId(),
                request.getCorrelationId(),
                request.getStartDate(),
                request.getEndDate(),
                pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditEvent getAuditEventById(Long id) {
        log.debug("Getting audit event by ID: {}", id);
        return auditEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit event not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public AuditEvent getAuditEventByEventId(String eventId) {
        log.debug("Getting audit event by event ID: {}", eventId);
        return auditEventRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Audit event not found with eventId: " + eventId));
    }

    @Override
    @Transactional
    public int deleteExpiredAuditEvents() {
        log.info("Deleting expired audit events");
        LocalDateTime now = LocalDateTime.now();
        List<AuditEvent> expiredEvents = auditEventRepository.findByRetentionDateBefore(now);
        int count = expiredEvents.size();

        if (count > 0) {
            auditEventRepository.deleteAll(expiredEvents);
            log.info("Deleted {} expired audit events", count);
        } else {
            log.debug("No expired audit events found");
        }

        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserAuditCount(String userId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return auditEventRepository.countByUserIdSince(userId, since);
    }

    @Override
    @Transactional(readOnly = true)
    public long getEventTypeCount(String eventType, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return auditEventRepository.countByEventTypeSince(eventType, since);
    }

    private Pageable createPageable(AuditQueryRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "timestamp";
        String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "DESC";

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }

    private LocalDateTime calculateRetentionDate(String complianceTag) {
        // Default retention: 7 years for compliance
        int retentionYears = 7;

        if (complianceTag != null) {
            switch (complianceTag.toUpperCase()) {
                case "SOX":
                    retentionYears = 7;
                    break;
                case "HIPAA":
                    retentionYears = 6;
                    break;
                case "GDPR":
                    retentionYears = 3;
                    break;
                case "PCI-DSS":
                    retentionYears = 3;
                    break;
                default:
                    retentionYears = 7;
            }
        }

        return LocalDateTime.now().plusYears(retentionYears);
    }
}
