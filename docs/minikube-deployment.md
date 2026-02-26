# 🖥️ Local Minikube Deployment Guide for Full-Stack Microservices App
This guide explains how to deploy, test, and tear down the local Minikube version of this microservices + Kafka + Kubernetes application.  
It’s ideal for local development and testing before deploying to AWS.

---

## 📋 Prerequisites
Before starting, make sure you have:
- Docker installed
- kubectl installed
- Minikube installed
- Your project repo cloned locally

---

## 1️⃣ Start Minikube
Start a Minikube cluster with enough resources for Kafka, MySQL, MongoDB, and your services.

```
minikube start --cpus=4 --memory=7500 --driver=podman
```
Notes:
- 4 CPUs and 7500 MB RAM ensures enough capacity for all services.
- You can adjust these values if your machine has less RAM.

---

## 2️⃣ Enable Ingress and LoadBalancer Support
```
minikube addons enable ingress  
minikube addons enable ingress-dns
```
---

## 3️⃣ Deploy the Application to Minikube
```
kubectl apply -k k8/overlays/minikube
```
This will:
- Deploy login-service, registration-service, employee-service
- Deploy Kafka, MongoDB, MySQL
- Use NodePort for service exposure
- Apply local PVCs for storage

---

## 4️⃣ Get Service URLs

To list NodePort URLs:
```
minikube service list
```
Example:

login-service -> http://192.168.49.2:30001  
registration-service -> http://192.168.49.2:30002  
employee-service -> http://192.168.49.2:30003  

You can also run:
```
minikube tunnel
```
This creates LoadBalancer-style access locally.

---

## 4️⃣➕ Accessing the Frontend Through 127.0.0.1  
If you have the frontend exposed through an Ingress or LoadBalancer and you run:
```
minikube tunnel
```
Minikube assigns a local IP that is reachable on your host machine.  
In this setup, the frontend becomes accessible directly at:
```
http://127.0.0.1/
```
or
```
http://127.0.0.1:<port>
```  
(if your frontend is using a NodePort or specific port mapping)

This allows you to open the UI in a browser exactly as it would appear in production.

---


## 5️⃣ SQL Database Setup (Local Minikube)

After deploying Minikube, load the test data into the two MySQL databases.

## 1. Port‑Forward the MySQL Services

Open two terminals:

```bash
kubectl port-forward svc/login-mysql 3307:3306
kubectl port-forward svc/employee-mysql 3308:3306
```

- Login DB → `localhost:3307`  
- Employee DB → `localhost:3308`

## 2. Connect Using MySQL Workbench

Create two connections:

**Login Database**
- Host: 127.0.0.1  
- Port: 3307  
- User: root  
- Password: from K8s secret  

**Employee Database**
- Host: 127.0.0.1  
- Port: 3308  
- User: root  
- Password: from K8s secret  

## 3. Run SQL Test Data Scripts

Execute the two scripts:

- `scripts/sql/insert_login_test_data.sql`
- `scripts/sql/insert_employee_test_data.sql`

These insert the test users and employee records required for local development.

---

## 6️⃣ Test the Application

Use curl, Postman, or a browser.

Example:
```
curl http://192.168.49.2:30001/api/login/status/check
```
You can also test Kafka flow:
- Register a user
- Approve the registration
- Check employee creation

---

## 7️⃣ Stop or Delete the Local Cluster

Pause cluster:
```
minikube pause
```
Stop cluster:
```
minikube stop
```
Delete cluster:
```
minikube delete
```
---

## ⏱️ Time and Cost
- Startup time: 2–4 minutes  
- Cost: free  
Great for rapid development without AWS charges.

---

## 💡 Tips
- Use Minikube for most development; only deploy to AWS for final demos.
- If RAM is limited, deploy services in smaller groups.