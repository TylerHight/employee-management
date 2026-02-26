package com.ibm.fscc.auditservice.repository;

import com.ibm.fscc.auditservice.model.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    Optional<AuditEvent> findByEventId(String eventId);

    Page<AuditEvent> findByEventType(String eventType, Pageable pageable);

    Page<AuditEvent> findByUserId(String userId, Pageable pageable);

    Page<AuditEvent> findByServiceName(String serviceName, Pageable pageable);

    Page<AuditEvent> findByAggregateId(String aggregateId, Pageable pageable);

    Page<AuditEvent> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT a FROM AuditEvent a WHERE " +
            "(:eventType IS NULL OR a.eventType = :eventType) AND " +
            "(:userId IS NULL OR a.userId = :userId) AND " +
            "(:serviceName IS NULL OR a.serviceName = :serviceName) AND " +
            "(:aggregateId IS NULL OR a.aggregateId = :aggregateId) AND " +
            "(:action IS NULL OR a.action = :action) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:resourceType IS NULL OR a.resourceType = :resourceType) AND " +
            "(:resourceId IS NULL OR a.resourceId = :resourceId) AND " +
            "(:correlationId IS NULL OR a.correlationId = :correlationId) AND " +
            "(:startDate IS NULL OR a.timestamp >= :startDate) AND " +
            "(:endDate IS NULL OR a.timestamp <= :endDate)")
    Page<AuditEvent> findByFilters(
            @Param("eventType") String eventType,
            @Param("userId") String userId,
            @Param("serviceName") String serviceName,
            @Param("aggregateId") String aggregateId,
            @Param("action") String action,
            @Param("status") String status,
            @Param("resourceType") String resourceType,
            @Param("resourceId") String resourceId,
            @Param("correlationId") String correlationId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    List<AuditEvent> findByRetentionDateBefore(LocalDateTime date);

    @Query("SELECT COUNT(a) FROM AuditEvent a WHERE a.userId = :userId AND a.timestamp >= :since")
    long countByUserIdSince(@Param("userId") String userId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(a) FROM AuditEvent a WHERE a.eventType = :eventType AND a.timestamp >= :since")
    long countByEventTypeSince(@Param("eventType") String eventType, @Param("since") LocalDateTime since);
}
