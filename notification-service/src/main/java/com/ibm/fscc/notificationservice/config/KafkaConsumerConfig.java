package com.ibm.fscc.notificationservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;

/**
 * Configuration for Kafka consumers.
 * Applies error handling to all Kafka listeners.
 */
@Configuration
public class KafkaConsumerConfig {

    @Autowired
    private CommonErrorHandler kafkaErrorHandler;

    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    /**
     * Creates a Kafka listener container factory with error handling.
     *
     * @return The configured listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        return factory;
    }
}