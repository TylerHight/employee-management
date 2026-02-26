package com.ibm.fscc.apigateway.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SecurityHeaderFilterTest {

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private HttpHeaders httpHeaders;

    private SecurityHeadersFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new SecurityHeadersFilter();

        when(exchange.getResponse()).thenReturn(response);

        when(response.getHeaders()).thenReturn(httpHeaders);

        when(chain.filter(exchange)).thenReturn(Mono.empty());
    }

    @Test
    void shouldAddAllSecurityHeaders() {
        filter.filter(exchange, chain);
        verify(httpHeaders).add("Content-Security-Policy",
                "default-src 'self'; script-src 'self' https://trusted-cdn.com");
        verify(httpHeaders).add("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        verify(httpHeaders).add("X-Content-Type-Options", "nosniff");
        verify(httpHeaders).add("X-Frame-Options", "DENY");
        verify(httpHeaders).add("Referrer-Policy", "no-referrer");
        verify(httpHeaders).add("Permissions-Policy", "geolocation=()");
    }

    @Test
    void shouldNotBreakFilterChain() {
        filter.filter(exchange, chain);
        verify(chain).filter(exchange); // Verify the chain continues
    }

    @Test
    void shouldHaveCorrectOrder() {
        assertEquals(Integer.MAX_VALUE, filter.getOrder()); // Verify it runs last
    }
}
