package com.ibm.fscc.loginservice.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class EmailDto {

    @Email
    @NotBlank
    @Size(min = 8, max = 35, message = "Email must be between 8 and 35 characters.")
    private String email;
}
