package com.ibm.fscc.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "eureka.client.enabled=false", // Disable Eureka for tests
    "spring.cloud.gateway.discovery.locator.enabled=false"
})
class ApiGatewayIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        // Verify application starts successfully
    }

    @Test
    void actuatorHealthEndpointIsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/actuator/health", 
            String.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void actuatorGatewayRoutesEndpointIsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/actuator/gateway/routes", 
            String.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

