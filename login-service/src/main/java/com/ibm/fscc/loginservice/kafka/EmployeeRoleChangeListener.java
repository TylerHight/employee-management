package com.ibm.fscc.loginservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.ibm.fscc.common.exception.ResourceNotFoundException;
import com.ibm.fscc.loginservice.model.LoginEntity;
import com.ibm.fscc.loginservice.repository.LoginRepository;
import com.ibm.fscc.kafka.dto.EmployeeRoleChangeEvent;

@Service
public class EmployeeRoleChangeListener {
    
    private static final Logger log = LoggerFactory.getLogger(EmployeeRoleChangeListener.class);

    private final LoginRepository loginRepository;

    public EmployeeRoleChangeListener(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @KafkaListener(topics = "employee-role-changed", groupId = "${spring.kafka.consumer.group-id}")
    public void handleEmployeeRoleChange(EmployeeRoleChangeEvent event) {
        log.info("Received EmployeeEventDto for EmployeeRoleChange in login-service with userId: {}", event.getUserId());

        // Change login record role
        LoginEntity login = loginRepository.findByUserId(event.getUserId());
        if (login == null) {
            throw new ResourceNotFoundException("LoginEntity", "userId", event.getUserId());
        }
        login.setRole(event.getNewRole());
        loginRepository.save(login);
    }
}
