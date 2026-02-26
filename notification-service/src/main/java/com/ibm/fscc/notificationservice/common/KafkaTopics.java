package com.ibm.fscc.notificationservice.common;

/**
 * Enum for Kafka topics used in the notification service.
 * This centralizes all topic names to avoid hardcoding and ensure consistency.
 */
public enum KafkaTopics {
    
    // Topics that this service consumes
    PASSWORD_SETUP_REQUESTED("password-setup-requested"),
    PASSWORD_RESET_REQUESTED("password-reset-requested"),
    
    // Dead letter topics
    DLT_PASSWORD_SETUP_REQUESTED("dlt.password-setup-requested"),
    DLT_PASSWORD_RESET_REQUESTED("dlt.password-reset-requested");
    
    private final String topicName;
    
    KafkaTopics(String topicName) {
        this.topicName = topicName;
    }
    
    public String topicName() {
        return topicName;
    }
}