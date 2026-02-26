package com.ibm.fscc.apigateway.filters;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();

        // Add security headers
        response.getHeaders().add("Content-Security-Policy", "default-src 'self'; script-src 'self' https://trusted-cdn.com");
        response.getHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        response.getHeaders().add("X-Content-Type-Options", "nosniff");
        response.getHeaders().add("X-Frame-Options", "DENY");
        response.getHeaders().add("Referrer-Policy", "no-referrer");
        response.getHeaders().add("Permissions-Policy", "geolocation=()");

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE; // Ensures this filter runs last
    }
}

