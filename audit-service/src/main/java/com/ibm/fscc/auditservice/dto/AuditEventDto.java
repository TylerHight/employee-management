package com.ibm.fscc.auditservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEventDto {
    private String eventId;
    private String eventType;
    private String aggregateType;
    private String aggregateId;
    private LocalDateTime timestamp;
    private String userId;
    private String userEmail;
    private String ipAddress;
    private String userAgent;
    private String serviceName;
    private String serviceVersion;
    private String correlationId;
    private String sessionId;
    private String action;
    private String status;
    private String beforeState;
    private String afterState;
    private String changes;
    private String errorMessage;
    private String metadata;
    private String resourceType;
    private String resourceId;
    private String httpMethod;
    private String requestPath;
    private Integer responseCode;
    private Long responseTimeMs;
    private String complianceTag;
}
