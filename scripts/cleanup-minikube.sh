#!/bin/bash

# Minikube Cleanup Script
# This script performs a complete cleanup of the Kubernetes deployment in Minikube
# Use this before redeploying to avoid PV/PVC mismatch issues

set -e

echo "=========================================="
echo "Minikube Cleanup Script"
echo "=========================================="
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    print_error "kubectl not found. Please install kubectl first."
    exit 1
fi

# Check if minikube is available
if ! command -v minikube &> /dev/null; then
    print_error "minikube not found. Please install minikube first."
    exit 1
fi

# Check if minikube is running
if ! minikube status &> /dev/null; then
    print_warning "Minikube is not running. Starting minikube..."
    minikube start
fi

echo "Step 1: Deleting all resources from minikube overlay..."
if kubectl delete -k k8/overlays/minikube --ignore-not-found=true; then
    print_status "Resources deleted successfully"
else
    print_warning "Some resources may not have been deleted (this is normal if they don't exist)"
fi

echo ""
echo "Step 2: Waiting for pods to terminate..."
kubectl wait --for=delete pod --all --timeout=120s 2>/dev/null || print_warning "Timeout waiting for pods (they may already be deleted)"
print_status "Pods terminated"

echo ""
echo "Step 3: Deleting all PersistentVolumes..."
if kubectl delete pv --all --ignore-not-found=true; then
    print_status "PersistentVolumes deleted"
else
    print_warning "No PersistentVolumes to delete"
fi

echo ""
echo "Step 4: Cleaning up Minikube hostPath storage..."
if minikube ssh "sudo rm -rf /data/kafka /data/zookeeper /tmp/hostpath-provisioner/*" 2>/dev/null; then
    print_status "Minikube storage cleaned"
else
    print_warning "Could not clean some storage paths (they may not exist)"
fi

echo ""
echo "Step 5: Verifying cleanup..."
echo ""
echo "Remaining PersistentVolumes:"
kubectl get pv 2>/dev/null || echo "None"
echo ""
echo "Remaining PersistentVolumeClaims:"
kubectl get pvc 2>/dev/null || echo "None"
echo ""
echo "Remaining Pods:"
kubectl get pods 2>/dev/null || echo "None"

echo ""
echo "=========================================="
print_status "Cleanup completed successfully!"
echo "=========================================="
echo ""
echo "You can now deploy with:"
echo "  kubectl apply -k k8/overlays/minikube"
echo ""
echo "To monitor the deployment:"
echo "  kubectl get pods -w"
echo ""


