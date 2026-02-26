package com.ibm.fscc.employeeservice.kafka;

import com.ibm.fscc.employeeservice.common.KafkaTopics;
import com.ibm.fscc.kafka.dto.EmployeeEmailChangedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmployeeEmailChangedProducer {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeEmailChangedProducer.class);

    private final KafkaTemplate<String, EmployeeEmailChangedEvent> kafkaTemplate;

    public EmployeeEmailChangedProducer(KafkaTemplate<String, EmployeeEmailChangedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmailChanged(EmployeeEmailChangedEvent event) {
        logger.info("Sending EmployeeEmailChanged event to Kafka for userId: {}", 
                    event.getUserId(), event.getOldEmail(), event.getNewEmail());
        kafkaTemplate.send(KafkaTopics.EMPLOYEE_EMAIL_CHANGED.topicName(), event.getUserId(), event);
    }
}