package com.ibm.fscc.kafka.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class EmployeeRoleChangeEvent {
    private String userId;
    private String newRole;
    private Instant changedAt;    
}
