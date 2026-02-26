package com.ibm.fscc.apigateway.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AuditLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AuditLoggingFilter.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${audit.kafka.topic:audit.api-gateway}")
    private String auditTopic;

    public AuditLoggingFilter(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long responseTime = System.currentTimeMillis() - startTime;

            try {
                Map<String, Object> auditEvent = buildAuditEvent(request, response, responseTime);
                String auditJson = objectMapper.writeValueAsString(auditEvent);

                kafkaTemplate.send(auditTopic, auditJson)
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                logger.error("Failed to send audit event to Kafka: {}", ex.getMessage());
                            } else {
                                logger.debug("Audit event sent to Kafka: {}", auditEvent.get("eventId"));
                            }
                        });
            } catch (Exception e) {
                logger.error("Error creating audit event: {}", e.getMessage(), e);
            }
        }));
    }

    private Map<String, Object> buildAuditEvent(ServerHttpRequest request, ServerHttpResponse response,
            long responseTime) {
        Map<String, Object> event = new HashMap<>();

        // Event metadata
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "API_GATEWAY_REQUEST");
        event.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        event.put("serviceName", "api-gateway");
        event.put("serviceVersion", "1.0.0");

        // Request details
        event.put("httpMethod", request.getMethod().toString());
        event.put("requestPath", request.getURI().getPath());
        event.put("action", request.getMethod().toString());

        // User details from headers (set by JWT filter)
        String userId = request.getHeaders().getFirst("X-User-Id");
        String userRole = request.getHeaders().getFirst("X-User-Role");
        event.put("userId", userId != null ? userId : "anonymous");
        event.put("metadata", String.format("{\"role\":\"%s\"}", userRole != null ? userRole : "NONE"));

        // Client details
        String ipAddress = getClientIp(request);
        event.put("ipAddress", ipAddress);
        event.put("userAgent", request.getHeaders().getFirst(HttpHeaders.USER_AGENT));

        // Response details
        event.put("responseCode", response.getStatusCode() != null ? response.getStatusCode().value() : 0);
        event.put("responseTimeMs", responseTime);
        event.put("status",
                response.getStatusCode() != null && response.getStatusCode().is2xxSuccessful() ? "SUCCESS" : "FAILURE");

        // Correlation
        String correlationId = request.getHeaders().getFirst("X-Correlation-Id");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        event.put("correlationId", correlationId);

        return event;
    }

    private String getClientIp(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
        }
        return ip;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE; // Run last to capture response
    }
}
