package com.ibm.fscc.employeeservice.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmployeeUpdateDto {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String cellPhone;
    private String homePhone;
    @Email(message = "Invalid email format")
    private String email;
    private String role; // ADMIN or USER

}
