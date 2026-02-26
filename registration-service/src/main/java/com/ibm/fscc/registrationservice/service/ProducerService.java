package com.ibm.fscc.registrationservice.service;

import com.ibm.fscc.kafka.dto.EmployeeEventDto;

public interface ProducerService {
    /**
     * Sends a registration event to the specified Kafka topic
     * @param topic The Kafka topic to send the event to
     * @param event The event data to send
     */
    void sendRegistrationEvent(String topic, EmployeeEventDto event);
    
    /**
     * Sends a failed message to the Dead Letter Topic for later processing
     * @param originalTopic The original topic where the message was intended to be sent
     * @param event The event data that failed to be processed
     * @param exception The exception that caused the failure
     */
    void sendToDLT(String originalTopic, EmployeeEventDto event, Exception exception);
}
