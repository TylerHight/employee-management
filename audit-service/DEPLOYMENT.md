# Audit Service Deployment Guide

This guide provides step-by-step instructions for deploying the Audit Service to a Minikube Kubernetes cluster.

## Prerequisites

- Minikube installed and running
- kubectl configured to use Minikube context
- Docker installed
- Maven installed (for building the service)

## Deployment Steps

### 1. Build the Audit Service

From the `audit-service` directory:

```bash
cd audit-service
mvn clean package -DskipTests
```

### 2. Build Docker Image

Build the Docker image and load it into Minikube:

```bash
# Build the Docker image
docker build -t audit-service:latest .

# Load the image into Minikube
minikube image load audit-service:latest
```

Alternatively, you can build directly in Minikube's Docker daemon:

```bash
# Point your shell to Minikube's Docker daemon
eval $(minikube docker-env)

# Build the image
docker build -t audit-service:latest .
```

### 3. Deploy to Kubernetes

From the project root directory:

```bash
# Apply all Kubernetes manifests
kubectl apply -k k8/base/

# Or apply audit-service resources specifically
kubectl apply -f k8/base/audit-service/
```

### 4. Verify Deployment

Check that all pods are running:

```bash
# Check audit service pods
kubectl get pods -l app=audit-service

# Check audit postgres pods
kubectl get pods -l app=audit-postgres

# Check all audit-related resources
kubectl get all -l app=audit-service
kubectl get all -l app=audit-postgres
```

Expected output:
```
NAME                                  READY   STATUS    RESTARTS   AGE
pod/audit-service-xxxxxxxxxx-xxxxx    1/1     Running   0          2m
pod/audit-postgres-xxxxxxxxxx-xxxxx   1/1     Running   0          2m
```

### 5. Check Logs

```bash
# Audit service logs
kubectl logs -f deployment/audit-service

# Audit postgres logs
kubectl logs -f deployment/audit-postgres
```

### 6. Access the Audit Service

#### Option A: Port Forward (for testing)

```bash
kubectl port-forward svc/audit-service 8085:8080
```

Then access the service at: `http://localhost:8085`

#### Option B: Through API Gateway

The audit service is accessible through the API Gateway at:
```
http://<api-gateway-url>/audit-service/**
```

### 7. Test the Audit Service

#### Health Check

```bash
curl http://localhost:8085/actuator/health
```

#### Query Audit Events (requires authentication)

```bash
# Get all audit events (paginated)
curl -H "Authorization: Bearer <your-jwt-token>" \
     http://localhost:8085/api/v1/audit/events?page=0&size=20

# Query by user
curl -H "Authorization: Bearer <your-jwt-token>" \
     http://localhost:8085/api/v1/audit/events?userId=user123

# Query by event type
curl -H "Authorization: Bearer <your-jwt-token>" \
     http://localhost:8085/api/v1/audit/events?eventType=USER_LOGIN

# Query by date range
curl -H "Authorization: Bearer <your-jwt-token>" \
     "http://localhost:8085/api/v1/audit/events?startDate=2025-01-01T00:00:00Z&endDate=2025-12-31T23:59:59Z"
```

## Kafka Topics

The audit service consumes from the following Kafka topics:

- `audit.events` - General audit events
- `audit.api-gateway` - API Gateway request/response events
- `audit.authentication` - Authentication events (login, logout, password changes)

### Verify Kafka Topics

```bash
# Port forward to AKHQ (Kafka UI)
kubectl port-forward svc/akhq 8080:8080

# Access AKHQ at http://localhost:8080
```

Or use kubectl to check topics:

```bash
kubectl exec -it deployment/kafka -- kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --list | grep audit
```

## Database Access

### Connect to Audit PostgreSQL

```bash
# Port forward to PostgreSQL
kubectl port-forward svc/audit-postgres 5433:5432

# Connect using psql
psql -h localhost -p 5433 -U audituser -d auditdb
# Password: auditpass123
```

### Verify Database Schema

```sql
-- List tables
\dt

-- Check audit_events table
\d audit_events

-- Query recent audit events
SELECT event_id, event_type, user_id, timestamp 
FROM audit_events 
ORDER BY timestamp DESC 
LIMIT 10;
```

## Troubleshooting

### Pod Not Starting

```bash
# Describe the pod to see events
kubectl describe pod <pod-name>

# Check logs
kubectl logs <pod-name>
```

Common issues:
- **ImagePullBackOff**: Image not loaded into Minikube. Run `minikube image load audit-service:latest`
- **CrashLoopBackOff**: Check logs for application errors. Often database connection issues.

### Database Connection Issues

```bash
# Check if PostgreSQL is running
kubectl get pods -l app=audit-postgres

# Check PostgreSQL logs
kubectl logs deployment/audit-postgres

# Verify secret exists
kubectl get secret audit-postgres-secret
kubectl describe secret audit-postgres-secret
```

### Kafka Connection Issues

```bash
# Check if Kafka is running
kubectl get pods -l app=kafka

# Check Kafka logs
kubectl logs deployment/kafka

# Verify audit service can reach Kafka
kubectl exec -it deployment/audit-service -- nc -zv kafka 9092
```

### No Audit Events Being Recorded

1. Check if Kafka topics exist:
```bash
kubectl exec -it deployment/kafka -- kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --list | grep audit
```

2. Check if API Gateway is publishing events:
```bash
kubectl logs deployment/api-gateway | grep -i audit
```

3. Check audit service consumer logs:
```bash
kubectl logs deployment/audit-service | grep -i consumer
```

## Scaling

### Scale Audit Service

```bash
# Scale to 3 replicas
kubectl scale deployment/audit-service --replicas=3

# Verify scaling
kubectl get pods -l app=audit-service
```

### Scale PostgreSQL (Not Recommended)

PostgreSQL is deployed as a single instance. For production, consider:
- Using a managed PostgreSQL service (AWS RDS, Azure Database, etc.)
- Setting up PostgreSQL with replication
- Using a StatefulSet instead of Deployment

## Monitoring

### Check Resource Usage

```bash
# CPU and memory usage
kubectl top pods -l app=audit-service
kubectl top pods -l app=audit-postgres
```

### View Metrics

```bash
# Prometheus metrics endpoint
kubectl port-forward svc/audit-service 8085:8080
curl http://localhost:8085/actuator/prometheus
```

## Cleanup

### Remove Audit Service

```bash
# Delete all audit-service resources
kubectl delete -f k8/base/audit-service/

# Or delete specific resources
kubectl delete deployment audit-service
kubectl delete deployment audit-postgres
kubectl delete service audit-service
kubectl delete service audit-postgres
kubectl delete configmap audit-service-config
kubectl delete secret audit-postgres-secret
kubectl delete pvc audit-postgres-pvc
```

### Verify Cleanup

```bash
kubectl get all -l app=audit-service
kubectl get all -l app=audit-postgres
```

## Production Considerations

### Security

1. **Change Default Passwords**: Update the PostgreSQL password in the secret
2. **Enable TLS**: Configure TLS for PostgreSQL connections
3. **Network Policies**: Restrict network access between services
4. **RBAC**: Configure proper Kubernetes RBAC for the audit service

### High Availability

1. **Multiple Replicas**: Run multiple audit-service instances
2. **Database Replication**: Set up PostgreSQL replication
3. **Persistent Storage**: Use appropriate storage class for PVCs
4. **Backup Strategy**: Implement regular database backups

### Performance

1. **Resource Limits**: Adjust CPU/memory limits based on load
2. **Database Tuning**: Optimize PostgreSQL configuration
3. **Kafka Partitions**: Increase partitions for higher throughput
4. **Indexing**: Add database indexes for common queries

### Compliance

1. **Data Retention**: Configure automatic cleanup of old audit events
2. **Encryption**: Enable encryption at rest for the database
3. **Access Logs**: Enable audit logging for the audit service itself
4. **Compliance Tags**: Ensure all events have appropriate compliance tags

## Integration with Other Services

The audit service automatically receives events from:

- **API Gateway**: All API requests/responses via `audit.api-gateway` topic
- **Login Service**: Authentication events via `audit.authentication` topic
- **Employee Service**: Employee CRUD operations via `audit.events` topic
- **Registration Service**: Registration events via `audit.events` topic
- **Notification Service**: Notification events via `audit.events` topic

No additional configuration is required in these services as they already publish to Kafka topics.

## Support

For issues or questions:
1. Check the logs: `kubectl logs deployment/audit-service`
2. Review the README.md for API documentation
3. Check Kafka topics in AKHQ: http://localhost:8080 (after port-forward)
4. Verify database connectivity: `kubectl exec -it deployment/audit-postgres -- psql -U audituser -d auditdb`