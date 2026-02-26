package com.ibm.fscc.loginservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private Long userId;
    private String email;
    private String role;
    private boolean passwordSet;
    private String token;
}
