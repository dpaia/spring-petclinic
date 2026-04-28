#!/bin/bash
set -e

# Set variables
IMAGE_NAME="spring-petclinic-native"
IMAGE_TAG="latest"
CONTAINER_NAME="spring-petclinic-test"
PORT=8080
TIMEOUT=60  # Maximum time to wait for the application to start (in seconds)

echo "Starting test for Spring PetClinic native container..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running or not accessible"
    exit 1
fi

# Check if the image exists
if [[ "$(docker images -q ${IMAGE_NAME}:${IMAGE_TAG} 2> /dev/null)" == "" ]]; then
    echo "Error: Image ${IMAGE_NAME}:${IMAGE_TAG} not found"
    echo "Please build the image first with: ./build-docker-image.sh"
    exit 1
fi

# Remove any existing test container
if [ "$(docker ps -a -q -f name=${CONTAINER_NAME})" ]; then
    echo "Removing existing test container..."
    docker stop ${CONTAINER_NAME} 2>/dev/null || true
    docker rm ${CONTAINER_NAME} 2>/dev/null || true
fi

echo "Starting container for testing..."
docker run --name ${CONTAINER_NAME} -p ${PORT}:${PORT} -d ${IMAGE_NAME}:${IMAGE_TAG}

# Wait for container to start
echo "Waiting for container to start..."
CONTAINER_ID=$(docker ps -q -f name=${CONTAINER_NAME})
if [ -z "$CONTAINER_ID" ]; then
    echo "Error: Container failed to start"
    exit 1
fi

# Check container logs for startup issues
echo "Checking container logs..."
docker logs ${CONTAINER_NAME}

# Wait for application to be ready
echo "Waiting for application to be ready (timeout: ${TIMEOUT}s)..."
START_TIME=$(date +%s)
while true; do
    CURRENT_TIME=$(date +%s)
    ELAPSED_TIME=$((CURRENT_TIME - START_TIME))
    
    if [ $ELAPSED_TIME -gt $TIMEOUT ]; then
        echo "Error: Application failed to start within ${TIMEOUT} seconds"
        docker stop ${CONTAINER_NAME}
        docker rm ${CONTAINER_NAME}
        exit 1
    fi
    
    # Try to access the application
    if curl -s http://localhost:${PORT} | grep -q "PetClinic"; then
        echo "Application is up and running!"
        break
    fi
    
    echo "Waiting for application to start... (${ELAPSED_TIME}s elapsed)"
    sleep 2
done

# Test specific endpoints
echo "Testing endpoints..."
ENDPOINTS=(
    "/"
    "/owners/find"
    "/vets.html"
)

for ENDPOINT in "${ENDPOINTS[@]}"; do
    echo "Testing endpoint: ${ENDPOINT}"
    RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${PORT}${ENDPOINT})
    if [ "$RESPONSE" == "200" ]; then
        echo "  ✅ Endpoint ${ENDPOINT} is accessible"
    else
        echo "  ❌ Endpoint ${ENDPOINT} returned status ${RESPONSE}"
        TESTS_FAILED=true
    fi
done

# Check memory usage
echo "Checking memory usage..."
MEMORY_USAGE=$(docker stats ${CONTAINER_NAME} --no-stream --format "{{.MemUsage}}")
echo "Memory usage: ${MEMORY_USAGE}"

# Clean up
echo "Cleaning up test container..."
docker stop ${CONTAINER_NAME}
docker rm ${CONTAINER_NAME}

if [ "$TESTS_FAILED" == "true" ]; then
    echo "❌ Some tests failed"
    exit 1
else
    echo "✅ All tests passed successfully!"
    exit 0
fi
