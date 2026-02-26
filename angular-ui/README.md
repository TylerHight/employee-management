# Angular UI Deployment Guide

This guide explains how to run the Angular UI in different environments:

1. **Local Development** — `ng serve` with hot reload  
2. **Containerized (Outside Kubernetes)** — Podman/Docker using `environment.prod.ts`  
3. **Kubernetes (Minikube)** — Kustomize overlays for deployment  
---

## 📂 Project Structure (Relevant Parts)

```
angular-ui/
  src/environments/
    environment.ts          # Local dev settings
    environment.prod.ts     # Production build settings
  Dockerfile                # Builds Angular in production mode and serves with NGINX
  nginx.conf                # NGINX config for SPA routing

k8/
  base/                     # Base Kubernetes manifests
  overlays/
    minikube/               # Minikube-specific patches
    aws/                    # AWS-specific patches
```

---

## 1️⃣ Local Development (`ng serve`)

This mode uses **`environment.ts`** and is best for active development.

**Steps:**
```bash
cd angular-ui
npm install
ng serve
```

**API URL Configuration:**
Edit `environment.ts`:
```ts
apiUrl: 'http://localhost:8080'
```
Use the URL for your local backend.

**Access:**
- Open [http://localhost:4200](http://localhost:4200)

---

## 2️⃣ Running in a Container Outside Kubernetes

The Dockerfile builds with `--configuration production`, so **`environment.prod.ts`** is used.

**Steps (Podman):**
```bash
cd angular-ui
podman build -t angular-ui .
podman run -p 8080:80 angular-ui
```

**Steps (Docker):**
```bash
docker build -t angular-ui .
docker run -p 8080:80 angular-ui
```

**API URL Configuration:**
Edit `environment.prod.ts`:
```ts
apiUrl: 'http://host.docker.internal:8080'   // backend on host machine
// OR
apiUrl: 'http://api-gateway:8080'            // backend in Docker network or cluster
```

**Access:**
- Open [http://localhost:8080](http://localhost:8080)

---

## 3️⃣ Running Inside Kubernetes (Minikube)

### 🚀 Deploy to Minikube Using `update-service.ps1`

Instead of manually building and applying Kubernetes manifests, you can use the provided PowerShell script to automate:

1. **Tagging** the image with both a specific version and `latest`
2. **Building** locally for Minikube (`Target=local`) or in Podman for cloud (`Target=cloud`)
3. **Updating** the Kubernetes `deployment.yaml` with the new image tag
4. **Applying** the updated deployment to the cluster
5. **Restarting** the deployment so the new image is pulled

#### Script Location
`update-service.ps1` — stored in scripts folder

#### Parameters
- `ServiceName` — The name of the service directory and Kubernetes deployment (e.g., `registration-service`).
- `Version` — The version tag for the Docker image (e.g., `1.0.3`).
- `Target` — Deployment target:
  - `local` — build inside Minikube (no push to Docker Hub)
  - `cloud` — build/push to Docker Hub for remote clusters

#### Examples

**Deploy `angular-ui` to Minikube with version `1.0.3`:**
```powershell
.\update-service.ps1 -ServiceName angular-ui -Version 1.0.3 -Target local
```

**Deploy `angular-ui` to a cloud cluster with version `1.0.3`:**
```powershell
.\update-service.ps1 -ServiceName angular-ui -Version 1.0.3 -Target cloud
```
**Access:**
```powershell
minikube service angular-ui
```
---
## 📌 Quick Commands Reference

**Local Dev:**
```bash
ng serve
```

**Prod Build:**
```bash
ng build --configuration production
```

**Container Local:**
```bash
docker build -t angular-ui .
docker run -p 8080:80 angular-ui
```

**Minikube:**
```bash
eval $(minikube docker-env)
docker build -t angular-ui:latest .
kubectl apply -k k8/overlays/minikube
minikube service angular-ui
```