package com.ibm.fscc.employeeservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity // For Spring Boot 3.x
public class MethodSecurityConfig {
}
