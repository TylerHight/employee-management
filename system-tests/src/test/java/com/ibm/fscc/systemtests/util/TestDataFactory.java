package com.ibm.fscc.systemtests.util;

import com.ibm.fscc.systemtests.dto.EmployeeDto;
import com.ibm.fscc.systemtests.dto.EmployeeUpdateDto;
import com.ibm.fscc.systemtests.dto.RegistrationRequestDto;

public class TestDataFactory {

    public static EmployeeDto buildEmployeeDto(String firstName, String lastName, String email) {
        EmployeeDto dto = new EmployeeDto();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setAddress("123 Test St");
        dto.setCity("Testville");
        dto.setState("IL");
        dto.setZip("62701");
        dto.setCellPhone("1234567890");
        dto.setHomePhone("0987654321");
        dto.setEmail(email);
        return dto;
    }

    public static EmployeeUpdateDto buildEmployeeUpdateDto(String firstName, String lastName) {
        EmployeeUpdateDto dto = new EmployeeUpdateDto();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        return dto;
    }

    public static RegistrationRequestDto buildRegistrationRequest(String firstName, String lastName, String email) {
        RegistrationRequestDto dto = new RegistrationRequestDto();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        return dto;
    }
}
