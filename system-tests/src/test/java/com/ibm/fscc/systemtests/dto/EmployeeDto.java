package com.ibm.fscc.systemtests.dto;

import lombok.Data;

@Data
public class EmployeeDto {
    private Long id;
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
