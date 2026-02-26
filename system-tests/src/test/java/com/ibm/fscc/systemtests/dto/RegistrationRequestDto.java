package com.ibm.fscc.systemtests.dto;

import lombok.Data;

@Data
public class RegistrationRequestDto {
    private String firstName;
    private String lastName;
    private String email;
}
