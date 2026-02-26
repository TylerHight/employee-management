# Kubernetes Manifests for Microservices Stack

This directory contains Kubernetes manifests for deploying the backend microservices and infrastructure for the project.

## 📂 Structure

```
k8/
  discovery-service/         # Eureka service discovery
  api-gateway/               # API Gateway (Spring Cloud Gateway or Zuul)
  login-service/             # Login service + MySQL
  employee-service/          # Employee service + MySQL
  registration-service/      # Registration service + MongoDB
  kafka/                     # Zookeeper + Kafka broker
  akhq/                      # Kafka UI monitoring
```

---

## 🚀 Deployment Order (Minikube)

Since some services depend on others, deploy in this order:

1. **Discovery Service**  
   `kubectl apply -f discovery-service/`

2. **API Gateway**  
   `kubectl apply -f api-gateway/`

3. **Databases**  
   - MySQL for Login Service  
   - MySQL for Employee Service  
   - MongoDB for Registration Service  

4. **Kafka + Zookeeper**  
   - Deploy Kafka cluster and Zookeeper before Registration Service.  

5. **Business Services**  
   - Login Service  
   - Employee Service  
   - Registration Service  

6. **AKHQ** (optional monitoring tool for Kafka)

---

## 🐳 Deploying to Minikube

Deploying a service:
```bash
# Start minikube (in an Admin Powershell)
minikube start --driver=podman --container-runtime=cri-o

# Build image
minikube image build -t <service-name>:latest ./<service-name>

# Confirm that the image was created
minikube image ls | findstr <service-name>

# Deploy service
kubectl apply -f k8/<service-name>/

# Check the pod
kubectl get pods

# Forward the port to your machine once the pod is ready
# (In most cases, you only need to port forward the api-gateway to access application functionality)
kubectl port-forward svc/<service-name> <local-port>:<container-port>
```
Deploying a database (example):
```bash
# 1. Create the secret with MongoDB credentials
kubectl apply -f k8/registration-mongodb/mongodb-secret.yaml

# 2. Create persistent storage for MongoDB
kubectl apply -f k8/registration-mongodb/mongodb-pvc.yaml

# 3. Deploy MongoDB
kubectl apply -f k8/registration-mongodb/deployment.yaml

# 4. Expose it inside the cluster
kubectl apply -f k8/registration-mongodb/service.yaml
```
Create a Kafka topic (Minikube will not create topics automatically)
```bash
kubectl run kafka-client --rm -it --image=bitnami/kafka:3.6.0 --restart=Never --command -- bash -c "kafka-topics.sh --create --topic registrations --bootstrap-server kafka:29092 --partitions 1 --replication-factor 1"
```
Other helpful commands:
```bash
# Example: Restart pod
kubectl delete pod -l app=<service-name>

# Full cleanup of cluster
kubectl delete all --all

# Stop Minikube entirely
minikube stop

# Delete the entire Minikube VM
minikube delete
```
---

## ☁️ Deploying to the Cloud

When deploying to a cloud Kubernetes cluster:
1. Build and push images to Docker Hub or another container registry.
   ```bash
   docker build -t mydockerhubusername/discovery-service:latest ./discovery-service
   docker push mydockerhubusername/discovery-service:latest
   ```
2. Update the `image:` field in each `deployment.yaml` to point to the registry image.
3. Apply manifests:
   ```bash
   kubectl apply -f .
   ```

---

## 🔍 Health Checks

All services should define:
- **Liveness Probe**: `/actuator/health`
- **Readiness Probe**: `/actuator/health`

These are already configured in the manifests.

---

## 🧹 Cleanup

To delete everything:
```bash
kubectl delete -f .
```

---

## 📌 Notes

- Use `ConfigMaps` for non-sensitive config values.
- Use `Secrets` for database passwords, API keys, etc.
- Persistent storage (`PersistentVolume` + `PersistentVolumeClaim`) is configured for MySQL and MongoDB.
