package com.ibm.fscc.apigateway.filters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import ch.qos.logback.classic.Logger;           
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;                  
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLoggingFilterTest {

    @Mock
    private GatewayFilterChain filterChain;

    private RequestLoggingFilter filter = new RequestLoggingFilter();

    @Test
    void shouldFilterRequest() {
        // Arrange
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test").build());
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
    }

    @Test
    void shouldReturnCorrectOrder() {
        assertEquals(-1, filter.getOrder());
    }

    @Test
    void shouldCreateLogs() {
        // Capture logs
        Logger logger = (Logger) LoggerFactory.getLogger(RequestLoggingFilter.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // Run filter
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test").build());
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        // Verify logs were created
        assertTrue(listAppender.list.size() > 0);
    }

}
