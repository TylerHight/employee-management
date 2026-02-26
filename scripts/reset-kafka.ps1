<#
.SYNOPSIS
    Resets Kafka and Zookeeper in a Minikube-based Kubernetes development environment.

.DESCRIPTION
    This script completely wipes persisted data for Kafka and Zookeeper stored in hostPath PersistentVolumes
    inside the Minikube VM, then redeploys them in the correct order to avoid cluster ID mismatches.
    It ensures Zookeeper starts first and is ready before Kafka starts, preventing InconsistentClusterIdException.

    Steps performed:
        1. Delete Kafka and Zookeeper Deployments (prevents auto-restarts during reset).
        2. SSH into Minikube and remove data from /data/kafka and /data/zookeeper.
        3. Re-apply Zookeeper manifest and wait until pod is ready.
        4. Re-apply Kafka manifest and wait until pod is ready.
        5. Print success message.

.PARAMETER None
    No parameters required.

.NOTES
    Author: Tyler Hight
    Date:   2025-10-14
    Requirements:
        - Minikube installed and running.
        - Kubernetes manifests for Kafka and Zookeeper in k8/base/.
        - PowerShell execution policy allowing script execution.
        - Access to kubectl CLI configured for Minikube context.

.EXAMPLE
    PS> .\reset-kafka.ps1
    Deleting Kafka and Zookeeper deployments...
    Connecting to Minikube and wiping PV data...
    Starting Zookeeper...
    Waiting for Zookeeper to be ready...
    Starting Kafka...
    Waiting for Kafka to be ready...
    Kafka and Zookeeper reset complete and running!
#>

Write-Host "Deleting Kafka and Zookeeper deployments..."
kubectl delete deployment kafka --ignore-not-found
kubectl delete deployment zookeeper --ignore-not-found

Write-Host "Connecting to Minikube and wiping PV data..."
minikube ssh "sudo rm -rf /data/kafka/* && sudo rm -rf /data/zookeeper/*"

Write-Host "Starting Zookeeper..."
kubectl apply -f ../k8/base/zookeeper/zookeeper.yaml

Write-Host "Waiting for Zookeeper to be ready..."
kubectl wait --for=condition=ready pod -l app=zookeeper --timeout=90s

Write-Host "Starting Kafka..."
kubectl apply -f ../k8/base/kafka/kafka.yaml

Write-Host "Waiting for Kafka to be ready..."
kubectl wait --for=condition=ready pod -l app=kafka --timeout=90s

Write-Host "Kafka and Zookeeper reset complete and running!"
