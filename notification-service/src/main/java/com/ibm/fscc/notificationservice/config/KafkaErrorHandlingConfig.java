package com.ibm.fscc.notificationservice.config;

import com.ibm.fscc.notificationservice.common.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Configuration for Kafka error handling.
 * Sets up dead letter topics and error handling strategies.
 */
@Configuration
@Slf4j
public class KafkaErrorHandlingConfig {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Creates a default error handler that publishes failed messages to dead letter topics.
     * Uses a fixed backoff strategy with 3 retry attempts.
     *
     * @return The configured error handler
     */
    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        // Create a recoverer that publishes to dead letter topics
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (consumerRecord, exception) -> {
                    String originalTopic = consumerRecord.topic();
                    String deadLetterTopic = determineDeadLetterTopic(originalTopic);
                    
                    log.error("Error processing message from topic {}, sending to dead letter topic {}. Error: {}",
                            originalTopic, deadLetterTopic, exception.getMessage());
                    
                    return new org.apache.kafka.common.TopicPartition(deadLetterTopic, consumerRecord.partition());
                });

        // Create error handler with retry logic
        // 1000ms interval, 3 retries
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer, 
                new FixedBackOff(1000L, 3L)
        );
        // Configure to log all failures by default
        
        
        return errorHandler;
    }
    
    /**
     * Determines the appropriate dead letter topic based on the original topic.
     *
     * @param originalTopic The original topic name
     * @return The corresponding dead letter topic name
     */
    private String determineDeadLetterTopic(String originalTopic) {
        if (originalTopic.equals(KafkaTopics.PASSWORD_SETUP_REQUESTED.topicName())) {
            return KafkaTopics.DLT_PASSWORD_SETUP_REQUESTED.topicName();
        } else if (originalTopic.equals(KafkaTopics.PASSWORD_RESET_REQUESTED.topicName())) {
            return KafkaTopics.DLT_PASSWORD_RESET_REQUESTED.topicName();
        } else {
            // Default fallback - should not happen in normal operation
            return "dlt." + originalTopic;
        }
    }
}