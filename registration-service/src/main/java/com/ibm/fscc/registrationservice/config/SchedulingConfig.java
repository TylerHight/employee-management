package com.ibm.fscc.registrationservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ibm.fscc.registrationservice.service.ConsumerService;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    private static final Logger log = LoggerFactory.getLogger(SchedulingConfig.class);

    private final ConsumerService consumerService;

    public SchedulingConfig(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    /**
     * Scheduled task to clean up processed message cache every 6 hours
     * This prevents memory leaks from the idempotence cache
     */
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000) // 6 hours
    public void cleanupProcessedMessages() {
        log.info("Running scheduled cleanup of processed message cache");
        consumerService.cleanupProcessedMessages();
    }
}
