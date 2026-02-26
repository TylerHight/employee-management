package com.ibm.fscc.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetEventDto {
    private String email;
    private String resetToken;
    private String resetLink;
}
