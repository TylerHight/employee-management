# Employee Management Full Stack Microservice Application

Microservices-based employee management application with event-driven workflows, an Angular frontend, and Kubernetes deployment targets for local and cloud environments.

## What This Application Does

This application manages the employee onboarding and administration lifecycle:

- New users submit registration requests
- Admins review and approve/reject registrations
- Approved users are provisioned in employee and login systems
- Login and password setup/reset workflows are handled through dedicated auth services
- Notification workflows send email events (for example, password setup/reset)
- Employee records can be viewed and managed through the UI and secured APIs
- Audit events are captured for traceability across service interactions

## Application Overview

- Frontend: `angular-ui`
- API entry point: `api-gateway`
- Service discovery: `discovery-service` (Eureka)
- Core domain services: `login-service`, `registration-service`, `employee-service`
- Supporting services: `notification-service`, `audit-service`
- Event backbone: Kafka + Zookeeper (with AKHQ UI)

## Technology Stack

- Backend: Java 17, Spring Boot, Spring Cloud (Gateway + Eureka), Spring Security, JWT, Spring Data JPA, Spring Data MongoDB, Spring Kafka
- Frontend: Angular 20 + Angular Material
- Databases: MySQL (login, employee), MongoDB (registration), PostgreSQL (audit)
- Platform/Infra: Docker, Docker Compose, Kubernetes, Kustomize, NGINX Ingress
- Testing: JUnit 5, Spock (Groovy), Rest Assured, Selenium + Cucumber + TestNG

## Cloud Services and Deployment Targets

- AWS EKS deployment path documented in [`docs/AWS-deployment.md`](docs/AWS-deployment.md)
- AWS infrastructure used in that flow includes:
  - EKS cluster + EC2 worker nodes
  - LoadBalancer services (AWS ELB)
  - Persistent volumes via AWS storage classes (`gp2`)
  - IAM-authenticated CLI tooling (`aws`, `eksctl`, `kubectl`)
- Container registry workflow uses Docker Hub (see `scripts/update-service.ps1`)
- Optional cloud sample in `aws-lambda-example/` demonstrates AWS Lambda + Secrets Manager + RDS Proxy access

## Quick Start

1. Local containers:
   - `docker compose up --build`
2. Local Kubernetes:
   - Follow [`docs/minikube-deployment.md`](docs/minikube-deployment.md)
3. AWS Kubernetes:
   - Follow [`docs/AWS-deployment.md`](docs/AWS-deployment.md)

## Key Docs

- [`docs/development-workflow.md`](docs/development-workflow.md)
- [`k8/README.md`](k8/README.md)
- [`scripts/update-service.ps1`](scripts/update-service.ps1)
