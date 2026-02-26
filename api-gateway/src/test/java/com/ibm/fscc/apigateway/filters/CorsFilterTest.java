package com.ibm.fscc.apigateway.filters;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.gateway.globalcors.cors-configurations.[/**].allowedOrigins=http://frontend.example.com",
    "spring.cloud.gateway.globalcors.cors-configurations.[/**].allowedMethods=GET,POST",
    "spring.cloud.gateway.globalcors.cors-configurations.[/**].allowedHeaders=Authorization,Content-Type",
    "spring.cloud.gateway.globalcors.cors-configurations.[/**].allowCredentials=true"
})
class CorsFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void validOriginAllowed() {
        webTestClient.options()
            .uri("/api/test")
            .header("Origin", "http://frontend.example.com")
            .header("Access-Control-Request-Method", "GET")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://frontend.example.com")
            .expectHeader().valueEquals("Access-Control-Allow-Credentials", "true");
    }

    @Test
    void invalidOriginRejected() {
        webTestClient.options()
            .uri("/api/test")
            .header("Origin", "http://malicious-site.com")
            .header("Access-Control-Request-Method", "GET")
            .exchange()
            .expectStatus().isForbidden(); // Should reject invalid origins
    }

    @Test
    void allowedMethodsWork() {
        webTestClient.options()
            .uri("/api/test")
            .header("Origin", "http://frontend.example.com")
            .header("Access-Control-Request-Method", "GET")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Access-Control-Allow-Methods", 
                methods -> methods.contains("GET"));
    }

    @Test
    void disallowedMethodsBlocked() {
        webTestClient.options()
            .uri("/api/test")
            .header("Origin", "http://frontend.example.com")
            .header("Access-Control-Request-Method", "DELETE")
            .exchange()
            .expectStatus().isForbidden(); // DELETE not in allowed methods
    }

    @Test
    void allowedHeadersWork() {
        webTestClient.options()
            .uri("/api/test")
            .header("Origin", "http://frontend.example.com")
            .header("Access-Control-Request-Method", "POST")
            .header("Access-Control-Request-Headers", "Authorization,Content-Type")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Access-Control-Allow-Headers",
                headers -> headers.contains("Authorization"));
    }
}
