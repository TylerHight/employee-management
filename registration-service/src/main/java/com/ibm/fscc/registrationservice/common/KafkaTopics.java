package com.ibm.fscc.registrationservice.common;

public enum KafkaTopics {
    // Employees that have been approved for onboarding
    EMPLOYEE_APPROVED("employee-approved"),
    
    // Dead letter topic for messages that couldn't be processed
    EMPLOYEE_EVENTS_DLT("employee-events-dlt");

    private final String topicName;

    KafkaTopics(String topicName) {
        this.topicName = topicName;
    }

    public String topicName() {
        return topicName;
    }
}

