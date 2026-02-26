package com.ibm.fscc.loginservice.common;

/**
 * Enum defining all Kafka topics used by the login service.
 * This ensures consistency and prevents typos in topic names.
 */
public enum KafkaTopics {
    // Topics that the login service consumes
    EMPLOYEE_APPROVED("employee-approved"),
    
    // Topics that the login service produces
    PASSWORD_RESET_REQUESTED("password-reset-requested"),
    PASSWORD_SETUP_REQUESTED("password-setup-requested");

    private final String topicName;

    KafkaTopics(String topicName) {
        this.topicName = topicName;
    }

    public String topicName() {
        return this.topicName;
    }
}