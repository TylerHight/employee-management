package com.ibm.fscc.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.gateway.discovery.locator.enabled=true",
    "spring.cloud.gateway.discovery.locator.lower-case-service-id=true",
    "eureka.client.enabled=false"
})
class RouteDiscoveryTest {

    @MockBean
    private DiscoveryClient discoveryClient;

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void shouldHaveRouteLocatorBean() {
        // Test that RouteLocator bean is properly configured
        assertThat(routeLocator).isNotNull();
    }

    @Test
    void shouldReturnRoutesFlux() {
        // Mock discovery client to return empty list
        when(discoveryClient.getServices()).thenReturn(Arrays.asList());

        // Test that routes flux is not null and can be subscribed to
        Flux<Route> routes = routeLocator.getRoutes();
        assertThat(routes).isNotNull();
        
        // Count routes (should complete without error)
        List<Route> routeList = routes.collectList().block();
        assertThat(routeList).isNotNull();
    }

}
