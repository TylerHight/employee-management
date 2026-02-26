package com.ibm.fscc.employeeservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ibm.fscc.employeeservice.common.KafkaTopics;

@Configuration
public class KafkaTopicConfig {

    @Bean
    NewTopic employeeRoleChangedTopic() {
        return new NewTopic(KafkaTopics.EMPLOYEE_ROLE_CHANGED.topicName(), 1, (short) 1);
    }
}
