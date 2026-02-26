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
public class AuditQueryRequest {
    private String eventType;
    private String userId;
    private String serviceName;
    private String aggregateId;
    private String action;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String resourceType;
    private String resourceId;
    private String correlationId;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
