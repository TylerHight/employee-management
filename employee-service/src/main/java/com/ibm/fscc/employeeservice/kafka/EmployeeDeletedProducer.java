package com.ibm.fscc.employeeservice.kafka;

import com.ibm.fscc.employeeservice.common.KafkaTopics;
import com.ibm.fscc.kafka.dto.EmployeeDeletedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmployeeDeletedProducer {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeDeletedProducer.class);

    private final KafkaTemplate<String, EmployeeDeletedEvent> kafkaTemplate;

    public EmployeeDeletedProducer(KafkaTemplate<String, EmployeeDeletedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmployeeDeleted(EmployeeDeletedEvent event) {
        logger.info("Sending EmployeeDeleted event to Kafka for userId: {} | email: {}", 
                    event.getUserId(), event.getEmail());
        kafkaTemplate.send(KafkaTopics.EMPLOYEE_DELETED.topicName(), event.getUserId(), event);
    }
}