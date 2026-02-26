package com.ibm.fscc.notificationservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Health indicator for Kafka connection.
 * Checks if the service can connect to Kafka brokers.
 */
@Component
@Slf4j
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaAdmin kafkaAdmin;
    
    @Autowired
    public KafkaHealthIndicator(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public Health health() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            // Set a timeout for the health check
            DescribeClusterOptions options = new DescribeClusterOptions().timeoutMs(5000);
            DescribeClusterResult result = adminClient.describeCluster(options);
            
            // Check if we can get cluster info within timeout
            String clusterId = result.clusterId().get(5, TimeUnit.SECONDS);
            int nodeCount = result.nodes().get(5, TimeUnit.SECONDS).size();
            
            return Health.up()
                    .withDetail("clusterId", clusterId)
                    .withDetail("nodeCount", nodeCount)
                    .build();
        } catch (Exception e) {
            log.error("Kafka health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}