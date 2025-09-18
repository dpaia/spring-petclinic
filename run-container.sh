#!/bin/bash
set -e

# Set variables
IMAGE_NAME="spring-petclinic-native"
IMAGE_TAG="latest"
CONTAINER_NAME="spring-petclinic"
PORT=8080

# Check if container already exists
if [ "$(docker ps -a -q -f name=${CONTAINER_NAME})" ]; then
    echo "Stopping and removing existing container..."
    docker stop ${CONTAINER_NAME} 2>/dev/null || true
    docker rm ${CONTAINER_NAME} 2>/dev/null || true
fi

echo "Starting Spring PetClinic native container..."
echo "Container name: ${CONTAINER_NAME}"
echo "Mapping port ${PORT} to ${PORT}"

# Run the container
docker run --name ${CONTAINER_NAME} \
    -p ${PORT}:${PORT} \
    -d \
    --restart=unless-stopped \
    ${IMAGE_NAME}:${IMAGE_TAG}

echo "Container started successfully!"
echo "The application should be available at: http://localhost:${PORT}"
echo "You can view logs with: docker logs -f ${CONTAINER_NAME}"
echo "You can stop the container with: docker stop ${CONTAINER_NAME}"
