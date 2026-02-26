package com.ibm.fscc.auditservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.fscc.auditservice.dto.AuditEventDto;
import com.ibm.fscc.auditservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventConsumer {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${audit.kafka.topics.audit-events}", groupId = "${audit.kafka.consumer.group-id}", containerFactory = "auditKafkaListenerContainerFactory")
    public void consumeAuditEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.debug("Received audit event from topic: {}, partition: {}, offset: {}", topic, partition, offset);

        try {
            AuditEventDto auditEventDto = objectMapper.readValue(message, AuditEventDto.class);
            auditService.saveAuditEvent(auditEventDto);
            log.info("Successfully processed audit event: {}", auditEventDto.getEventType());
        } catch (Exception e) {
            log.error("Error processing audit event from topic {}: {}", topic, e.getMessage(), e);
            // In production, you might want to send this to a dead letter queue
        }
    }

    @KafkaListener(topics = "${audit.kafka.topics.api-gateway-events}", groupId = "${audit.kafka.consumer.group-id}", containerFactory = "auditKafkaListenerContainerFactory")
    public void consumeApiGatewayEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        log.debug("Received API Gateway event from topic: {}", topic);

        try {
            AuditEventDto auditEventDto = objectMapper.readValue(message, AuditEventDto.class);
            auditService.saveAuditEvent(auditEventDto);
            log.info("Successfully processed API Gateway event: {}", auditEventDto.getEventType());
        } catch (Exception e) {
            log.error("Error processing API Gateway event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${audit.kafka.topics.authentication-events}", groupId = "${audit.kafka.consumer.group-id}", containerFactory = "auditKafkaListenerContainerFactory")
    public void consumeAuthenticationEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        log.debug("Received authentication event from topic: {}", topic);

        try {
            AuditEventDto auditEventDto = objectMapper.readValue(message, AuditEventDto.class);
            auditService.saveAuditEvent(auditEventDto);
            log.info("Successfully processed authentication event: {}", auditEventDto.getEventType());
        } catch (Exception e) {
            log.error("Error processing authentication event: {}", e.getMessage(), e);
        }
    }
}
