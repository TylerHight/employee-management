package com.ibm.fscc.registrationservice.service;

import com.ibm.fscc.kafka.dto.EmployeeEventDto;
import org.springframework.kafka.support.Acknowledgment;

public interface ConsumerService {
    /**
     * Consumes an employee event from Kafka and processes it
     * @param event The employee event to process
     * @param acknowledgment The Kafka acknowledgment to manually acknowledge the message
     */
    void consumeEmployeeEvent(EmployeeEventDto event, Acknowledgment acknowledgment);
    
    /**
     * Cleans up the processed message cache to prevent memory leaks
     */
    void cleanupProcessedMessages();
}
