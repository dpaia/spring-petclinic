#!/bin/bash
set -e

# Set variables
IMAGE_NAME="spring-petclinic-native"
IMAGE_TAG="latest"
GITHUB_USERNAME=""
GITHUB_REPO="spring-petclinic"

# Check if GitHub username is provided
if [ -z "$1" ]; then
    echo "Error: GitHub username is required"
    echo "Usage: $0 <github_username>"
    exit 1
else
    GITHUB_USERNAME="$1"
fi

# Full image name for GitHub Container Registry
GHCR_IMAGE="ghcr.io/${GITHUB_USERNAME}/${GITHUB_REPO}/${IMAGE_NAME}:${IMAGE_TAG}"

echo "Preparing to push image to GitHub Container Registry..."
echo "Target image: ${GHCR_IMAGE}"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running or not accessible"
    exit 1
fi

# Check if the local image exists
if [[ "$(docker images -q ${IMAGE_NAME}:${IMAGE_TAG} 2> /dev/null)" == "" ]]; then
    echo "Error: Local image ${IMAGE_NAME}:${IMAGE_TAG} not found"
    echo "Please build the image first with: ./build-docker-image.sh"
    exit 1
fi

# Check if user is logged in to GitHub Container Registry
if ! docker info | grep -q "ghcr.io"; then
    echo "You need to log in to GitHub Container Registry first"
    echo "Run: echo \$GITHUB_TOKEN | docker login ghcr.io -u ${GITHUB_USERNAME} --password-stdin"
    echo "Note: You need a GitHub Personal Access Token with 'write:packages' scope"
    exit 1
fi

# Tag the image for GitHub Container Registry
echo "Tagging image for GitHub Container Registry..."
docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${GHCR_IMAGE}

# Push the image to GitHub Container Registry
echo "Pushing image to GitHub Container Registry..."
docker push ${GHCR_IMAGE}

echo "✅ Image successfully pushed to GitHub Container Registry!"
echo "You can pull it with: docker pull ${GHCR_IMAGE}"
echo "You can run it with: docker run -p 8080:8080 ${GHCR_IMAGE}"

# Create a Kubernetes deployment YAML file
echo "Creating Kubernetes deployment YAML file..."
cat > kubernetes-deployment.yaml << EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-petclinic
  labels:
    app: spring-petclinic
spec:
  replicas: 1
  selector:
    matchLabels:
      app: spring-petclinic
  template:
    metadata:
      labels:
        app: spring-petclinic
    spec:
      containers:
      - name: spring-petclinic
        image: ${GHCR_IMAGE}
        ports:
        - containerPort: 8080
        resources:
          limits:
            cpu: "1"
            memory: "512Mi"
          requests:
            cpu: "0.5"
            memory: "256Mi"
        livenessProbe:
          httpGet:
            path: /
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: spring-petclinic
spec:
  selector:
    app: spring-petclinic
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
EOF

echo "✅ Kubernetes deployment YAML file created: kubernetes-deployment.yaml"
echo "You can deploy it with: kubectl apply -f kubernetes-deployment.yaml"
