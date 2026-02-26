package com.ibm.fscc.employeeservice.dto;

import lombok.Data;

@Data
public class EmployeeResponseDto {
    private long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String cellPhone;
    private String homePhone;
    private String email;
}

