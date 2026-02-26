package com.ibm.fscc.registrationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ibm.fscc.registrationservice.common.KafkaTopics;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic employeeApprovedTopic() {
        return new NewTopic(KafkaTopics.EMPLOYEE_APPROVED.topicName(), 1, (short) 1);
    }
}
