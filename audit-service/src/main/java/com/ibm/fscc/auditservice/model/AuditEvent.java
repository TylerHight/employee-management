package com.ibm.fscc.auditservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audit_events", indexes = {
        @Index(name = "idx_event_type", columnList = "eventType"),
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_timestamp", columnList = "timestamp"),
        @Index(name = "idx_service", columnList = "serviceName"),
        @Index(name = "idx_aggregate_id", columnList = "aggregateId")
})
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String eventId;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(length = 100)
    private String aggregateType;

    @Column(length = 100)
    private String aggregateId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 100)
    private String userId;

    @Column(length = 100)
    private String userEmail;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(nullable = false, length = 50)
    private String serviceName;

    @Column(length = 50)
    private String serviceVersion;

    @Column(length = 100)
    private String correlationId;

    @Column(length = 100)
    private String sessionId;

    @Column(length = 20)
    private String action;

    @Column(length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String beforeState;

    @Column(columnDefinition = "TEXT")
    private String afterState;

    @Column(columnDefinition = "TEXT")
    private String changes;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(length = 100)
    private String resourceType;

    @Column(length = 100)
    private String resourceId;

    @Column(length = 50)
    private String httpMethod;

    @Column(length = 500)
    private String requestPath;

    @Column
    private Integer responseCode;

    @Column
    private Long responseTimeMs;

    @Column(length = 50)
    private String complianceTag;

    @Column
    private LocalDateTime retentionDate;
}
