package com.ibm.fscc.employeeservice.kafka;

import com.ibm.fscc.employeeservice.common.KafkaTopics;
import com.ibm.fscc.kafka.dto.EmployeeEventDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmployeeAdminAddedProducer {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeAdminAddedProducer.class);

    private final KafkaTemplate<String, EmployeeEventDto> kafkaTemplate;

    public EmployeeAdminAddedProducer(KafkaTemplate<String, EmployeeEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmployeeAdminAdded(EmployeeEventDto event) {
        logger.info("Sending EmployeeAdminAdded event to Kafka for userId: {} | email: {}", 
                    event.getUserId(), event.getEmail());
        kafkaTemplate.send(KafkaTopics.EMPLOYEE_ADMIN_ADDED.topicName(), event.getUserId(), event);
    }
}