# General Development Workflow

This document describes the standard workflow for developing, building, and deploying services in the project. It applies to:

- Spring Boot backend services  
- The Angular UI service  
- Local development using Minikube  
- Cloud deployment using Docker Hub

---

## 1. Preparation

Before building or deploying anything, ensure the following:

- **Minikube** is installed and running (for local development).
- **kubectl** is configured for the correct cluster.
- **Podman** is installed and logged into Docker Hub (for cloud deployment).
- The PowerShell script `update-service.ps1` is accessible in the project workspace.

---

## 2. Developing Spring Boot Services

For backend services (e.g., `login-service`, `registration-service`):

1. Make code changes inside the service directory.
2. Build the application:
   ```
   mvn clean package
   ```
3. Deploy the updated service:
   ```
   .\update-service.ps1 -ServiceName <service-name>
   ```

### Notes

- If no `-Version` is provided, the script **auto-increments** the version based on `deployment.yaml`.
- The script defaults to **cloud deployment** (**Docker Hub** + remote cluster).
- To deploy locally to Minikube:
  ```
  .\update-service.ps1 -ServiceName <service-name> -Target local
  ```
- **Minikube must be running** when using `-Target local`.

---

## 3. Developing the Angular UI Service

The workflow is similar for the Angular UI service, with an additional build step.

1. Make UI changes.
2. Build the Angular application:
   ```
   ng build
   ```
3. Deploy the UI:
   ```
   .\update-service.ps1 -ServiceName ui-service
   ```
   or:
   ```
   .\update-service.ps1 -ServiceName ui-service -Target local
   ```

The script then:

- Builds the UI container image  
- Tags it with the version and `latest`  
- Loads into Minikube (local) or pushes to Docker Hub (cloud)  
- Updates `deployment.yaml`  
- Applies the Kubernetes update  
- Restarts the deployment  

---

## 4. Local Development (Minikube)

When targeting local:

- Image is built **inside Minikube**  
- No push to Docker Hub  
- Kubernetes automatically uses the newly built local image  

Example command:

```
.\update-service.ps1 -ServiceName login-service -Target local
```

This workflow is best for fast iteration.

---

## 5. Cloud Deployment

When targeting cloud:

- Podman builds the image  
- The script pushes both the versioned tag and `latest` to Docker Hub  
- Kubernetes pulls the new image from Docker Hub  

Example:

```
.\update-service.ps1 -ServiceName login-service -Version 1.0.3 -Target cloud
```

If no version is specified, the script auto-increments it.

---

## 6. Versioning Logic

If `-Version` is omitted, the script:

- Reads the current version from `deployment.yaml`
- Assumes semantic versioning: **major.minor.patch**
- Increments automatically using rules such as:

| Current | Next |
|--------|------|
| 1.1.9  | 1.2.0 |
| 1.2.9  | 1.3.0 |
| 1.9.9  | 2.0.0 |

---

## 7. Summary

**Backend services:**

```
mvn clean package
.\update-service.ps1 -ServiceName <service-name> [-Target local|cloud]
```

**UI service:**

```
ng build
.\update-service.ps1 -ServiceName ui-service [-Target local|cloud]
```

The script manages:

- Building images  
- Tagging  
- Pushing or loading images  
- Updating Kubernetes manifests  
- Applying changes  
- Restarting deployments  