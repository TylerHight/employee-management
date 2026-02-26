package com.ibm.fscc.apigateway.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Autowired
    private RouteDefinitionLocator locator;

    /**
     * Main API Gateway OpenAPI definition
     */
    @Bean
    public OpenAPI apiGatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FSCC Microservices API Gateway")
                        .description("Unified API documentation for all FSCC microservices. " +
                                "This gateway aggregates Employee Service, Registration Service, " +
                                "Login Service, and Notification Service APIs.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tyler Hight")
                                .email("tyler.hight@ibm.com")
                                .url("https://github.ibm.com/Tyler-Hight"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }

    /**
     * Group APIs by microservice
     * This creates separate tabs in Swagger UI for each service
     */
    @Bean
    public List<GroupedOpenApi> apis() {
        List<GroupedOpenApi> groups = new ArrayList<>();
        
        // Get all routes from the gateway
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        
        if (definitions != null) {
            definitions.stream()
                    .filter(routeDefinition -> routeDefinition.getId().matches(".*-service"))
                    .forEach(routeDefinition -> {
                        String name = routeDefinition.getId().replace("-service", "");
                        groups.add(GroupedOpenApi.builder()
                                .group(name)
                                .pathsToMatch("/" + routeDefinition.getId() + "/**")
                                .build());
                    });
        }
        
        // Manually add known services if auto-discovery doesn't work
        if (groups.isEmpty()) {
            groups.add(GroupedOpenApi.builder()
                    .group("employee")
                    .pathsToMatch("/employee-service/**")
                    .build());
            
            groups.add(GroupedOpenApi.builder()
                    .group("registration")
                    .pathsToMatch("/registration-service/**")
                    .build());
            
            groups.add(GroupedOpenApi.builder()
                    .group("login")
                    .pathsToMatch("/login-service/**")
                    .build());
            
            groups.add(GroupedOpenApi.builder()
                    .group("notification")
                    .pathsToMatch("/notification-service/**")
                    .build());
        }
        
        return groups;
    }
}