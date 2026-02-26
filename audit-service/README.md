# Audit Service

## Overview

The Audit Service is a centralized audit logging microservice that captures and stores audit events from all services in the FSCC application. It provides comprehensive audit trails for compliance, security monitoring, and troubleshooting.

## Features

- **Event Sourcing**: Captures all system events in an immutable audit log
- **Kafka Integration**: Consumes audit events from multiple Kafka topics
- **PostgreSQL Storage**: Stores audit events with full-text search capabilities
- **REST API**: Query and analyze audit logs with flexible filtering
- **Compliance Support**: Configurable retention policies (SOX, HIPAA, GDPR, PCI-DSS)
- **Performance**: Indexed queries for fast retrieval
- **Security**: Role-based access control (ADMIN only)

## Architecture

```
Services → Kafka Topics → Audit Service → PostgreSQL
                              ↓
                         Query API (REST)
```

### Kafka Topics

- `audit.events` - General audit events from all services
- `audit.api-gateway` - API Gateway request/response logs
- `audit.authentication` - Authentication and authorization events

## Technology Stack

- **Framework**: Spring Boot 3.3.5
- **Database**: PostgreSQL 16
- **Message Broker**: Apache Kafka
- **Service Discovery**: Eureka
- **API Documentation**: SpringDoc OpenAPI

## Database Schema

### AuditEvent Table

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| eventId | VARCHAR(36) | Unique event identifier (UUID) |
| eventType | VARCHAR(100) | Type of event (e.g., USER_LOGIN, EMPLOYEE_CREATED) |
| aggregateType | VARCHAR(100) | Entity type affected |
| aggregateId | VARCHAR(100) | Entity ID affected |
| timestamp | TIMESTAMP | When the event occurred |
| userId | VARCHAR(100) | User who triggered the event |
| userEmail | VARCHAR(100) | User's email address |
| ipAddress | VARCHAR(45) | Client IP address |
| userAgent | VARCHAR(500) | Client user agent |
| serviceName | VARCHAR(50) | Service that generated the event |
| action | VARCHAR(20) | Action performed (CREATE, UPDATE, DELETE, etc.) |
| status | VARCHAR(20) | Event status (SUCCESS, FAILURE) |
| beforeState | TEXT | State before change (JSON) |
| afterState | TEXT | State after change (JSON) |
| changes | TEXT | Detailed changes (JSON) |
| httpMethod | VARCHAR(50) | HTTP method (for API calls) |
| requestPath | VARCHAR(500) | Request path |
| responseCode | INTEGER | HTTP response code |
| responseTimeMs | BIGINT | Response time in milliseconds |
| complianceTag | VARCHAR(50) | Compliance category (SOX, HIPAA, etc.) |
| retentionDate | TIMESTAMP | When the event can be deleted |

## API Endpoints

### Query Audit Events

```http
GET /api/audit/events
```

**Query Parameters:**
- `eventType` - Filter by event type
- `userId` - Filter by user ID
- `serviceName` - Filter by service name
- `aggregateId` - Filter by aggregate ID
- `action` - Filter by action
- `status` - Filter by status
- `resourceType` - Filter by resource type
- `resourceId` - Filter by resource ID
- `correlationId` - Filter by correlation ID
- `startDate` - Filter by start date (ISO 8601)
- `endDate` - Filter by end date (ISO 8601)
- `page` - Page number (default: 0)
- `size` - Page size (default: 20)
- `sortBy` - Sort field (default: timestamp)
- `sortDirection` - Sort direction (ASC/DESC, default: DESC)

**Example:**
```bash
curl -X GET "http://localhost:8084/api/audit/events?userId=user123&startDate=2025-01-01T00:00:00&page=0&size=20" \
  -H "Authorization: Bearer <admin-token>"
```

### Get Audit Event by ID

```http
GET /api/audit/events/{id}
```

### Get Audit Event by Event ID

```http
GET /api/audit/events/event-id/{eventId}
```

### Get User Audit Statistics

```http
GET /api/audit/stats/user/{userId}?days=30
```

### Get Event Type Statistics

```http
GET /api/audit/stats/event-type/{eventType}?days=30
```

### Delete Expired Events

```http
DELETE /api/audit/cleanup
```

## Configuration

### application.properties

```properties
# Application
spring.application.name=audit-service
server.port=8084

# PostgreSQL
spring.datasource.url=jdbc:postgresql://audit-db:5432/audit_db
spring.datasource.username=user
spring.datasource.password=password

# Kafka
spring.kafka.bootstrap-servers=kafka:29092
audit.kafka.topics.audit-events=audit.events
audit.kafka.topics.api-gateway-events=audit.api-gateway
audit.kafka.topics.authentication-events=audit.authentication
audit.kafka.consumer.group-id=audit-service-group

# Eureka
eureka.client.service-url.defaultZone=http://discovery-service:8761/eureka/
```

## Retention Policies

The service automatically calculates retention dates based on compliance tags:

| Compliance Tag | Retention Period |
|----------------|------------------|
| SOX | 7 years |
| HIPAA | 6 years |
| GDPR | 3 years |
| PCI-DSS | 3 years |
| Default | 7 years |

## Event Types

### Authentication Events
- `USER_LOGIN` - User login attempt
- `USER_LOGOUT` - User logout
- `PASSWORD_CHANGE` - Password changed
- `PASSWORD_RESET` - Password reset requested
- `TOKEN_REFRESH` - JWT token refreshed

### Employee Events
- `EMPLOYEE_CREATED` - New employee created
- `EMPLOYEE_UPDATED` - Employee information updated
- `EMPLOYEE_DELETED` - Employee deleted
- `EMPLOYEE_ROLE_CHANGED` - Employee role modified

### Registration Events
- `REGISTRATION_SUBMITTED` - New registration submitted
- `REGISTRATION_APPROVED` - Registration approved
- `REGISTRATION_REJECTED` - Registration rejected

### API Gateway Events
- `API_GATEWAY_REQUEST` - API request processed

## Running the Service

### With Docker Compose

```bash
docker-compose up audit-service
```

### Standalone

```bash
cd audit-service
mvn clean package
java -jar target/audit-service-0.0.1-SNAPSHOT.jar
```

### Running Tests

```bash
mvn test
```

## Monitoring

### Health Check

```bash
curl http://localhost:8084/actuator/health
```

### Metrics

```bash
curl http://localhost:8084/actuator/metrics
```

## API Documentation

Swagger UI is available at:
```
http://localhost:8084/swagger-ui.html
```

OpenAPI specification:
```
http://localhost:8084/v3/api-docs
```

## Security

- All endpoints require authentication
- Only users with `ADMIN` role can access audit logs
- Audit events are immutable (no updates or deletes except for retention cleanup)
- Sensitive data should be masked before logging

## Best Practices

1. **Event Publishing**: Services should publish audit events asynchronously to avoid performance impact
2. **Data Masking**: Mask sensitive data (passwords, SSNs, credit cards) before publishing
3. **Correlation IDs**: Always include correlation IDs for request tracing
4. **Retention**: Set appropriate compliance tags for automatic retention management
5. **Monitoring**: Monitor Kafka consumer lag and database growth

## Troubleshooting

### Kafka Connection Issues

Check Kafka connectivity:
```bash
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Database Connection Issues

Check PostgreSQL connectivity:
```bash
docker exec -it audit-db psql -U user -d audit_db -c "SELECT COUNT(*) FROM audit_events;"
```

### High Memory Usage

Adjust JVM settings in Dockerfile:
```dockerfile
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
```

## Future Enhancements

- [ ] Elasticsearch integration for advanced search
- [ ] Real-time alerting for suspicious activities
- [ ] Data export functionality (CSV, JSON)
- [ ] Audit report generation
- [ ] Anomaly detection using ML
- [ ] Archive old events to S3/cold storage

## License

Copyright © 2025 IBM FSCC