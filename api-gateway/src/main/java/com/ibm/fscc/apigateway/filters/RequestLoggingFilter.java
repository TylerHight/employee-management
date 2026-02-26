package com.ibm.fscc.apigateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().toString();
        
        logger.info("Request: {} {} at {}", method, requestPath, LocalDateTime.now());
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            logger.info("Response: {} {} status {} at {}", 
                method, 
                requestPath, 
                exchange.getResponse().getStatusCode(),
                LocalDateTime.now());
        }));
    }
    
    @Override
    public int getOrder() {
        // Execute early in the filter chain
        return -1;
    }
}
