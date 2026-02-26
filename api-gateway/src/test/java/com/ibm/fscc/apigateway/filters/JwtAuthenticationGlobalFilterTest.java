package com.ibm.fscc.apigateway.filters;

import com.ibm.fscc.apigateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationGlobalFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationGlobalFilter filter;

    @Test
    void shouldSkipAuthenticationForLoginPath() {
        // Arrange
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/login/authenticate").build());
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    void shouldSkipAuthenticationForStatusCheck() {
        // Arrange
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/employees/status/check").build());
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
    }

    @Test
    void shouldReturnUnauthorizedWhenNoAuthHeader() {
        // Arrange
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/employees/123").build());

        // Act & Assert
        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(filterChain, never()).filter(exchange);
    }

    @Test
    void shouldReturnUnauthorizedForInvalidTokenFormat() {
        // Arrange
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/employees/123")
                        .header(HttpHeaders.AUTHORIZATION, "Basic invalidtoken")
                        .build());

        // Act & Assert
        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldPassThroughWithValidToken() {
        // Arrange - Create mock BEFORE the when() calls
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.get("userId", String.class)).thenReturn("123");
        when(mockClaims.get("role", String.class)).thenReturn("USER");

        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/employees/123")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer validtoken")
                        .build());

        // Mock the JWT util methods
        when(jwtUtil.validateToken("validtoken")).thenReturn(true);
        when(jwtUtil.extractClaims("validtoken")).thenReturn(mockClaims); // Use the pre-created mock
        when(filterChain.filter(any())).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(jwtUtil).validateToken("validtoken");
        verify(filterChain).filter(any());
    }

    @Test
    void shouldReturnCorrectOrder() {
        assertEquals(0, filter.getOrder());
    }
}
