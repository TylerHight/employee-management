package com.ibm.fscc.employeeservice.kafka;

import com.ibm.fscc.employeeservice.common.KafkaTopics;
import com.ibm.fscc.kafka.dto.EmployeeRoleChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmployeeRoleChangeProducer {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeRoleChangeProducer.class);

    private final KafkaTemplate<String, EmployeeRoleChangeEvent> kafkaTemplate;

    public EmployeeRoleChangeProducer(KafkaTemplate<String, EmployeeRoleChangeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRoleChange(EmployeeRoleChangeEvent event) {
        logger.info("Sending EmployeeRoleChanged event to Kafka for userId: {} | newRole: {}", 
                    event.getUserId(), event.getNewRole());
        kafkaTemplate.send(KafkaTopics.EMPLOYEE_ROLE_CHANGED.topicName(), event.getUserId(), event);
    }
}
