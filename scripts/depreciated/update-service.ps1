# Usage example:
# Local dev (Minikube): .\update-service.ps1 -ServiceName login-service -Version 1.0.1 -Target local
# Cloud deploy:         .\update-service.ps1 -ServiceName login-service -Version 1.0.1 -Target cloud
# Auto-increment:       .\update-service.ps1 -ServiceName login-service -Target local
# Defaults (auto-increment + cloud): .\update-service.ps1 -ServiceName login-service

<#
.SYNOPSIS
    Builds, tags, and deploys a service to Kubernetes with a new Docker image version, 
    supporting both local (Minikube) and cloud deployments.

.DESCRIPTION
    This script automates the process of:
      1. Building a container image for a given service
         - local: using Minikube's built-in image builder
         - cloud: using Podman
      2. Tagging it with both a specific version and 'latest'
      3. Either:
           - local: load into Minikube automatically
           - cloud: push to Docker Hub
      4. Updating the Kubernetes deployment.yaml with the new image tag
      5. Applying the updated deployment to the cluster
      6. Restarting the deployment to pull the new image

.PARAMETER ServiceName
    The name of the service directory and Kubernetes deployment (e.g., 'registration-service').

.PARAMETER Version
    The version tag to apply to the Docker image (e.g., '1.0.3').
    If not provided, the script will auto-increment the current version from deployment.yaml.
    Auto-increment logic: 1.1.9 -> 1.2.0, 1.2.9 -> 1.3.0, 1.9.9 -> 2.0.0

.PARAMETER Target
    Deployment target. Accepts:
      - "local" for Minikube development (image built inside Minikube, no push to Docker Hub)
      - "cloud" for pushing to Docker Hub for remote clusters (default)

.EXAMPLE
    .\update-service.ps1 -ServiceName registration-service -Version 1.0.3 -Target local

    Builds and deploys registration-service to Minikube with version 1.0.3.

.EXAMPLE
    .\update-service.ps1 -ServiceName registration-service -Version 1.0.3 -Target cloud

    Builds and deploys registration-service to a cloud cluster with version 1.0.3.

.NOTES
    REQUIREMENTS:
      - PowerShell 5+ or PowerShell Core
      - For cloud: Podman installed and logged into Docker Hub (`podman login docker.io`)
      - For local: Minikube installed and running
      - kubectl installed and configured to point to the target cluster
      - Correct file paths in $ServicePath and $DeploymentYaml
#>

param(
    [string]$ServiceName,        # e.g. registration-service
    [string]$Version = "",       # e.g. 1.0.3 (optional - will auto-increment if not provided)
    [ValidateSet("local", "cloud")]
    [string]$Target = "cloud"    # local or cloud (defaults to cloud)
)

# -----------------------------
# 0. Capture starting directory
# -----------------------------
$OriginalLocation = Get-Location

# -----------------------------
# 1. Validate input
# -----------------------------
if (-not $ServiceName) {
    Write-Host "Usage: .\update-service.ps1 -ServiceName <name> [-Version <version>] [-Target <local|cloud>]" -ForegroundColor Yellow
    Write-Host "Example with version: .\update-service.ps1 -ServiceName registration-service -Version 1.0.3 -Target cloud"
    Write-Host "Example auto-increment: .\update-service.ps1 -ServiceName registration-service -Target local"
    Write-Host "Example defaults (auto-increment + cloud): .\update-service.ps1 -ServiceName registration-service"
    exit 1
}

# -----------------------------
# 2. Config
# -----------------------------
$DOCKERHUB_USER = "tylerhight"
$ServicePath = "c:\Development\Projects\Practice\full-stack-coding-challenge\full-stack-coding-challenge\$ServiceName"
$DeploymentYaml = "c:\Development\Projects\Practice\full-stack-coding-challenge\full-stack-coding-challenge\k8\base\$ServiceName\deployment.yaml"

# -----------------------------
# 3. Validate paths
# -----------------------------
if (-not (Test-Path $ServicePath)) {
    Write-Host "ERROR: Service folder not found: $ServicePath" -ForegroundColor Red
    exit 1
}
if (-not (Test-Path $DeploymentYaml)) {
    Write-Host "ERROR: Deployment YAML not found: $DeploymentYaml" -ForegroundColor Red
    exit 1
}

# -----------------------------
# 4. Auto-increment version if not provided
# -----------------------------
if (-not $Version) {
    Write-Host "=== Auto-incrementing version ===" -ForegroundColor Cyan
    
    # Read current version from deployment.yaml
    $deploymentContent = Get-Content $DeploymentYaml -Raw
    if ($deploymentContent -match "docker\.io/${DOCKERHUB_USER}/${ServiceName}:(\d+)\.(\d+)\.(\d+)") {
        $major = [int]$matches[1]
        $minor = [int]$matches[2]
        $patch = [int]$matches[3]
        
        Write-Host "Current version: $major.$minor.$patch" -ForegroundColor Yellow
        
        # Increment logic: when patch reaches 9, increment minor and reset patch
        # when minor reaches 9, increment major and reset minor
        if ($patch -eq 9) {
            $patch = 0
            if ($minor -eq 9) {
                $minor = 0
                $major++
            } else {
                $minor++
            }
        } else {
            $patch++
        }
        
        $Version = "$major.$minor.$patch"
        Write-Host "New version: $Version" -ForegroundColor Green
    } else {
        Write-Host "ERROR: Could not find current version in deployment.yaml" -ForegroundColor Red
        Write-Host "Please provide a version manually using -Version parameter" -ForegroundColor Yellow
        exit 1
    }
}

# -----------------------------
# 5. Build image tags
# -----------------------------
$imageVersionTag = "docker.io/${DOCKERHUB_USER}/${ServiceName}:${Version}"
$imageLatestTag  = "docker.io/${DOCKERHUB_USER}/${ServiceName}:latest"

# -----------------------------
# 6. Navigate to service folder
# -----------------------------
Set-Location $ServicePath

# -----------------------------
# 7. Build and push/load image
# -----------------------------
if ($Target -eq "cloud") {
    Write-Host "=== Building ${ServiceName}:${Version} with Podman ===" -ForegroundColor Cyan
    podman build -t $imageVersionTag -t $imageLatestTag .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Build failed. Aborting." -ForegroundColor Red
        exit 1
    }

    Write-Host "=== Pushing ${imageVersionTag} and :latest to Docker Hub ===" -ForegroundColor Cyan
    podman push $imageVersionTag
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Push of version tag failed. Aborting." -ForegroundColor Red
        exit 1
    }
    podman push $imageLatestTag
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Push of latest tag failed. Aborting." -ForegroundColor Red
        exit 1
    }
}
elseif ($Target -eq "local") {
    Write-Host "=== Building ${ServiceName}:${Version} inside Minikube ===" -ForegroundColor Cyan
    minikube image build -t $imageVersionTag .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Minikube build (version tag) failed. Aborting." -ForegroundColor Red
        exit 1
    }

    Write-Host "=== Building ${ServiceName}:latest inside Minikube ===" -ForegroundColor Cyan
    minikube image build -t $imageLatestTag .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Minikube build (latest tag) failed. Aborting." -ForegroundColor Red
        exit 1
    }
}

# -----------------------------
# 8. Update deployment.yaml
# -----------------------------
Write-Host "=== Updating deployment.yaml to use ${imageVersionTag} ===" -ForegroundColor Cyan
(Get-Content $DeploymentYaml) -replace "(docker\.io\/${DOCKERHUB_USER}\/${ServiceName}:)([^\s]+)", "${imageVersionTag}" | Set-Content $DeploymentYaml

# -----------------------------
# 9. Apply changes to Kubernetes
# -----------------------------
Write-Host "=== Applying deployment to Kubernetes ===" -ForegroundColor Cyan
kubectl apply -f $DeploymentYaml

# -----------------------------
# 10. Restart deployment so new image is pulled
# -----------------------------
Write-Host "=== Restarting deployment ${ServiceName} ===" -ForegroundColor Cyan
kubectl rollout restart deployment $ServiceName

# -----------------------------
# 11. Return to original directory
# -----------------------------
Write-Host "=== Returning to original directory ===" -ForegroundColor Cyan
Set-Location $OriginalLocation
Write-Host "Done." -ForegroundColor Green
