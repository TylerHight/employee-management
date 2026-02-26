package com.ibm.fscc.registrationservice.config;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.ibm.fscc.registrationservice.exception.SyncException;

/**
 * Health indicator that monitors synchronization between Kafka and MongoDB
 * It tracks errors and provides health status based on error rates
 */
@Component
public class KafkaMongoSyncHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(KafkaMongoSyncHealthIndicator.class);

    // Error counters
    private final AtomicInteger kafkaPublishErrors = new AtomicInteger(0);
    private final AtomicInteger mongoDbErrors = new AtomicInteger(0);
    private final AtomicInteger syncErrors = new AtomicInteger(0);

    // Last error timestamps
    private LocalDateTime lastKafkaError;
    private LocalDateTime lastMongoError;
    private LocalDateTime lastSyncError;

    // Thresholds for health status
    @Value("${kafka.sync.error-threshold-warning:5}")
    private int errorThresholdWarning = 5;

    @Value("${kafka.sync.error-threshold-down:10}")
    private int errorThresholdDown = 10;

    @Value("${kafka.sync.error-threshold-critical:15}")
    private int errorThresholdCritical = 15;

    @Value("${kafka.sync.throw-exception-on-critical:true}")
    private boolean throwExceptionOnCritical = true;

    @Override
    public Health health() {
        int kafkaErrors = kafkaPublishErrors.get();
        int mongoErrors = mongoDbErrors.get();
        int syncErrors = this.syncErrors.get();

        Map<String, Object> details = new HashMap<>();
        details.put("kafkaPublishErrors", kafkaErrors);
        details.put("mongoDbErrors", mongoErrors);
        details.put("syncErrors", syncErrors);

        if (lastKafkaError != null) {
            details.put("lastKafkaError", lastKafkaError.toString());
        }
        if (lastMongoError != null) {
            details.put("lastMongoError", lastMongoError.toString());
        }
        if (lastSyncError != null) {
            details.put("lastSyncError", lastSyncError.toString());
        }

        // Reset counters if last error was more than 1 hour ago
        resetCountersIfNeeded();

        // Determine health status based on error counts
        if (kafkaErrors >= errorThresholdDown ||
                mongoErrors >= errorThresholdDown ||
                syncErrors >= errorThresholdDown) {
            return Health.down().withDetails(details).build();
        } else if (kafkaErrors >= errorThresholdWarning ||
                mongoErrors >= errorThresholdWarning ||
                syncErrors >= errorThresholdWarning) {
            return Health.status("WARNING").withDetails(details).build();
        } else {
            return Health.up().withDetails(details).build();
        }
    }

    /**
     * Record a Kafka publish error
     */
    public void recordKafkaError() {
        kafkaPublishErrors.incrementAndGet();
        lastKafkaError = LocalDateTime.now();
        log.warn("Kafka publish error recorded. Total count: {}", kafkaPublishErrors.get());
    }

    /**
     * Record a MongoDB error
     */
    public void recordMongoError() {
        mongoDbErrors.incrementAndGet();
        lastMongoError = LocalDateTime.now();
        log.warn("MongoDB error recorded. Total count: {}", mongoDbErrors.get());
    }

    /**
     * Record a synchronization error between Kafka and MongoDB
     *
     * @param message Error message describing the synchronization issue
     * @param cause   The underlying cause of the synchronization error (optional)
     * @throws SyncException if the error count exceeds the critical threshold and
     *                       throwExceptionOnCritical is true
     */
    public void recordSyncError(String message, Throwable cause) {
        int currentCount = syncErrors.incrementAndGet();
        lastSyncError = LocalDateTime.now();
        log.warn("Kafka-MongoDB sync error recorded. Total count: {}", currentCount);

        // Check if we've reached a critical threshold and should throw an exception
        if (throwExceptionOnCritical && currentCount >= errorThresholdCritical) {
            throw new SyncException(
                    "Critical synchronization error between Kafka and MongoDB: " + message,
                    cause);
        }
    }

    /**
     * Record a synchronization error between Kafka and MongoDB
     *
     * @throws SyncException if the error count exceeds the critical threshold and
     *                       throwExceptionOnCritical is true
     */
    public void recordSyncError() {
        recordSyncError("Synchronization error detected", null);
    }

    /**
     * Check if the system is in a critical state due to synchronization errors
     *
     * @return true if any error counter exceeds the critical threshold
     */
    public boolean isInCriticalState() {
        return kafkaPublishErrors.get() >= errorThresholdCritical ||
                mongoDbErrors.get() >= errorThresholdCritical ||
                syncErrors.get() >= errorThresholdCritical;
    }

    /**
     * Reset error counters if the last error was more than 1 hour ago
     */
    private void resetCountersIfNeeded() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minus(1, ChronoUnit.HOURS);

        if (lastKafkaError != null && lastKafkaError.isBefore(oneHourAgo)) {
            kafkaPublishErrors.set(0);
        }

        if (lastMongoError != null && lastMongoError.isBefore(oneHourAgo)) {
            mongoDbErrors.set(0);
        }

        if (lastSyncError != null && lastSyncError.isBefore(oneHourAgo)) {
            syncErrors.set(0);
        }
    }
}
