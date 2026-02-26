package com.ibm.fscc.loginservice.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class PasswordUpdateDto {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 8, max = 35, message = "Password must be between 8 and 35 characters.")
    private String newPassword;
}
