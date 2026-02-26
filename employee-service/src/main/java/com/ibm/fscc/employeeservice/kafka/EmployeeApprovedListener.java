package com.ibm.fscc.employeeservice.kafka;

import com.ibm.fscc.kafka.dto.EmployeeEventDto;
import com.ibm.fscc.employeeservice.services.EmployeeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmployeeApprovedListener {
    private static final Logger log = LoggerFactory.getLogger(EmployeeApprovedListener.class);

    private final EmployeeService employeeService;

    public EmployeeApprovedListener(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @KafkaListener(topics = "employee-approved", groupId = "employee-service")
    public void handleEmployeeApproved(EmployeeEventDto event) {
        log.info("Received employee approved event for: {}", event.getEmail());
        employeeService.addEmployeeFromApprovedEvent(event);
    }
}
