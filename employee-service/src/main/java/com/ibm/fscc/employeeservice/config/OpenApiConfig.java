package com.ibm.fscc.employeeservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for Employee Service API documentation.
 * Provides interactive API documentation accessible at /swagger-ui.html
 * 
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI employeeServiceOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .components(securityComponents())
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    private Info apiInfo() {
        return new Info()
                .title("Employee Service API")
                .description("""
                        Employee Management Service for Full-Stack Coding Challenge
                        
                        This service provides comprehensive employee management capabilities including:
                        - Employee CRUD operations
                        - Role management and authorization
                        - Integration with Kafka for event-driven architecture
                        - RESTful API endpoints with JWT authentication
                        
                        **Authentication:** All endpoints require JWT Bearer token authentication.
                        **Authorization:** Role-based access control (ADMIN, USER roles).
                        """)
                .version("1.0.0")
                .contact(apiContact())
                .license(apiLicense());
    }

    private Contact apiContact() {
        return new Contact()
                .name("Tyler Hight")
                .email("tyler.hight@ibm.com")
                .url("https://github.ibm.com/Tyler-Hight/full-stack-coding-challenge/tree/master/employee-service");
    }

    private License apiLicense() {
        return new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");
    }

    private List<Server> serverList() {
        return List.of(
                new Server()
                        .url("http://localhost:8080/api/employees")
                        .description("Local Development Server"),
                new Server()
                        .url("http://127.0.0.1:8080/api/employees")
                        .description("Minikube Tunnel")
        );
    }

    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT authentication token. Format: Bearer {token}")
                );
    }
}