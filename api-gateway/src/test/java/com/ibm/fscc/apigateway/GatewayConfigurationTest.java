package com.ibm.fscc.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.gateway.discovery.locator.enabled=true",
    "spring.cloud.gateway.discovery.locator.lower-case-service-id=true"
})
class GatewayConfigurationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void gatewayStartsSuccessfully() {
        // Test that gateway context loads without errors
        assertNotNull(routeLocator);
        assertTrue(port > 0);
    }

    @Test
    void discoveryLocatorCreatesRoutes() {
        // Test that discovery locator actually creates routes
        var routes = routeLocator.getRoutes().collectList().block();
        assertNotNull(routes);
        // Should have at least some routes if discovery is working
        assertFalse(routes.isEmpty());
    }

    @Test
    void gatewayRespondsToHealthCheck() {
        // Test basic gateway functionality
        webTestClient.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP");
    }
}
