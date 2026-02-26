package com.ibm.fscc.employeeservice.common;

public enum KafkaTopics {
    // Employees whose role has been changed by an admin
    EMPLOYEE_ROLE_CHANGED("employee-role-changed"),
    // Employees that have been added by an admin
    EMPLOYEE_ADMIN_ADDED("employee-admin-added"),
    // Employees that have been deleted
    EMPLOYEE_DELETED("employee-deleted"),
    // Employees whose email has been changed
    EMPLOYEE_EMAIL_CHANGED("employee-email-changed");

    private final String topicName;

    KafkaTopics(String topicName) {
        this.topicName = topicName;
    }

    public String topicName() {
        return topicName;
    }
}

