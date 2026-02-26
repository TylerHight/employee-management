package com.ibm.fscc.auditservice.controller;

import com.ibm.fscc.auditservice.dto.AuditQueryRequest;
import com.ibm.fscc.auditservice.model.AuditEvent;
import com.ibm.fscc.auditservice.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Audit", description = "Audit log management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/events")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Query audit events", description = "Search and filter audit events with pagination")
    public ResponseEntity<Page<AuditEvent>> queryAuditEvents(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String aggregateId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) String correlationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        log.info("Querying audit events with filters");

        AuditQueryRequest request = AuditQueryRequest.builder()
                .eventType(eventType)
                .userId(userId)
                .serviceName(serviceName)
                .aggregateId(aggregateId)
                .action(action)
                .status(status)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .correlationId(correlationId)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<AuditEvent> events = auditService.queryAuditEvents(request);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit event by ID", description = "Retrieve a specific audit event by its ID")
    public ResponseEntity<AuditEvent> getAuditEventById(@PathVariable Long id) {
        log.info("Getting audit event by ID: {}", id);
        AuditEvent event = auditService.getAuditEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/events/event-id/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit event by event ID", description = "Retrieve a specific audit event by its event ID")
    public ResponseEntity<AuditEvent> getAuditEventByEventId(@PathVariable String eventId) {
        log.info("Getting audit event by event ID: {}", eventId);
        AuditEvent event = auditService.getAuditEventByEventId(eventId);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/stats/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user audit statistics", description = "Get audit event count for a specific user")
    public ResponseEntity<Map<String, Object>> getUserAuditStats(
            @PathVariable String userId,
            @RequestParam(defaultValue = "30") int days) {

        log.info("Getting audit stats for user: {} for last {} days", userId, days);
        long count = auditService.getUserAuditCount(userId, days);

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("days", days);
        stats.put("eventCount", count);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/event-type/{eventType}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get event type statistics", description = "Get audit event count for a specific event type")
    public ResponseEntity<Map<String, Object>> getEventTypeStats(
            @PathVariable String eventType,
            @RequestParam(defaultValue = "30") int days) {

        log.info("Getting audit stats for event type: {} for last {} days", eventType, days);
        long count = auditService.getEventTypeCount(eventType, days);

        Map<String, Object> stats = new HashMap<>();
        stats.put("eventType", eventType);
        stats.put("days", days);
        stats.put("eventCount", count);

        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete expired audit events", description = "Remove audit events that have passed their retention date")
    public ResponseEntity<Map<String, Object>> cleanupExpiredEvents() {
        log.info("Cleaning up expired audit events");
        int deletedCount = auditService.deleteExpiredAuditEvents();

        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", deletedCount);
        result.put("message", "Successfully deleted " + deletedCount + " expired audit events");

        return ResponseEntity.ok(result);
    }
}
