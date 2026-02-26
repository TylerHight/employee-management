# 🚀 AWS EKS Deployment Guide

This guide explains how to **deploy**, **test/demo**, and **destroy** the AWS EKS version of this microservices + Kafka + Kubernetes application.  
It’s optimized for **learning/portfolio building** where **no persistent data** is required.

---

## 📋 Prerequisites

Before starting, make sure you have:

- **AWS Account** with billing enabled
- **IAM user/role** with `AdministratorAccess` or EKS/EC2 permissions
- **AWS CLI** installed and configured  
  ```bash
  aws configure
  ```
- **kubectl** installed ([Install Guide](https://kubernetes.io/docs/tasks/tools/))
- **eksctl** installed ([Install Guide](https://eksctl.io/))
- **Docker** installed (only needed if rebuilding images)
- Your **project repo** cloned locally

---

## 1️⃣ Create an EKS Cluster

> This spins up the EKS control plane, worker nodes, and networking for your application.

```bash
eksctl create cluster --name fscc-cluster --version 1.29 --region us-east-1 --nodegroup-name fscc-nodes --node-type t3.small --nodes 2
```

**Notes:**
- `t3.small` is inexpensive for dev/test purposes.
- This process takes about **10–15 minutes**.

---

## 2️⃣ Configure kubectl for the New Cluster

```bash
aws eks update-kubeconfig --region us-east-1 --name fscc-cluster
```

Verify:
```bash
kubectl get nodes
```

You should see 2 worker nodes in **Ready** state.

---

## 3️⃣ Deploy the Application to AWS

This repo already has an **AWS Kustomize overlay**.

```bash
kubectl apply -k k8/overlays/aws
```

This will:
- Deploy **login-service**, **registration-service**, **employee-service**
- Deploy **Kafka**, **MongoDB**, **MySQL**
- Configure **LoadBalancer** services for public access
- Apply **AWS-specific storage classes** (`gp2`) for PVCs

---

## 4️⃣ Wait for LoadBalancer IPs

AWS takes a few minutes to assign public IPs.

Check:
```bash
kubectl get svc
```

Look for your services with `EXTERNAL-IP` populated.  
Example output:
```
NAME                  TYPE           CLUSTER-IP      EXTERNAL-IP       PORT(S)        
login-service         LoadBalancer   10.0.123.45     a1b2c3d4.elb.amazonaws.com   8080:32709/TCP
registration-service  LoadBalancer   10.0.200.50     a5e6f7g8.elb.amazonaws.com   8080:31245/TCP
employee-service      LoadBalancer   10.0.180.22     a9h1i2j3.elb.amazonaws.com   8080:32415/TCP
```

---

## 5️⃣ Test the Application

- Use **Postman**, **curl**, or a browser to hit the public endpoints.
- Example:
  ```bash
  curl http://<api-gateway-external-ip>:8080/api/login/status/check
  ```
- You can also test Kafka event flow by registering a user, approving the registration, and verifying employee creation.

---

## 6️⃣ Record/Demonstrate the Demo

Since this is for **portfolio purposes**, record your screen showing:
- EKS cluster creation
- `kubectl apply` deployment
- Services getting public IPs
- Successful API calls between services
- Kafka events triggering cross-service actions

Tools:  
- **OBS Studio** (free)  
- Built-in screen recorder (Windows Game Bar, macOS QuickTime)

---

## 7️⃣ Destroy the Cluster to Stop All Costs

When you’re done testing/demoing:

```bash
eksctl delete cluster --name fscc-cluster
```

This will:
- Remove the EKS control plane
- Terminate all worker nodes
- Delete LoadBalancers
- Delete PVCs (since no persistence is needed)

---

## ⏱️ Time & Cost Estimates

**Time to Deploy:** ~20–30 minutes  
**Time to Destroy:** ~5–10 minutes  

**Cost per 2-hour demo session:**  
- EKS Control Plane: ~$0.20 for 2 hours  
- 2 × t3.small nodes: ~$0.10 for 2 hours  
- 3 LoadBalancers: ~$0.13 for 2 hours  
**Total:** **~$0.40–$0.50 per session**

---

## 💡 Tips for Saving Money

- Always destroy the cluster after testing.
- If you need to keep it but pause workloads, you can scale nodes to 0:
  ```bash
  eksctl scale nodegroup --cluster fscc-cluster --name fscc-nodes --nodes 0
  ```
  *(Still pays $74/month control plane fee if cluster exists)*
- Use smaller instance types (`t3.micro`) if traffic is light.

---

## ✅ Summary

For **learning and portfolio demos**:
- Deploy → Test → Record → Destroy is the cheapest and cleanest approach.
- No persistent data means you can recreate everything from scratch quickly.