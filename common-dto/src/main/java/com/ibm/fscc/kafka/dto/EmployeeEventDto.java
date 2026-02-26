package com.ibm.fscc.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEventDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
