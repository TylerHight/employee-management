package com.ibm.fscc.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for password setup events.
 * Contains all necessary information for sending a password setup email.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordSetupEventDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String setupToken;
    private String setupUrl;
}
