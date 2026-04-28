#!/bin/bash
set -e

# Set variables
IMAGE_NAME="spring-petclinic-native"
IMAGE_TAG="latest"

echo "Building Docker image for Spring PetClinic native application..."
echo "Image name: ${IMAGE_NAME}:${IMAGE_TAG}"

# Build the Docker image
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -f Dockerfile.native .

echo "Docker image built successfully!"
echo "You can run the container with: ./run-container.sh"
echo "Or manually with: docker run -p 8080:8080 ${IMAGE_NAME}:${IMAGE_TAG}"
