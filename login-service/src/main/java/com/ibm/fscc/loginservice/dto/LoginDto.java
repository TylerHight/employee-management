package com.ibm.fscc.loginservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    private Long id;
    private String userId;
    private String email;
    private String password;
    private String role;
    private boolean passwordSet; 
}
