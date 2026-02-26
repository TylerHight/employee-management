package com.ibm.fscc.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

        @Bean
        public RouteLocator apiRoutes(RouteLocatorBuilder builder) {
                return builder.routes()
                                // Login service route. Unprotected so users can authenticate
                                .route("login-service-route", r -> r
                                                .path("/api/login/**")
                                                .uri("lb://login-service"))
                                // Employee service route
                                .route("employee-service-route", r -> r
                                                .path("/api/employees/**")
                                                .uri("lb://employee-service"))
                                // Registration service route
                                .route("registration-service-route", r -> r
                                                .path("/api/registration/**")
                                                .uri("lb://registration-service"))
                                .build();
        }
}
