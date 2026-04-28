# Spring PetClinic Native Image in a Minimal Docker Container

This document provides instructions for building and running the Spring PetClinic application as a GraalVM native image in a minimal Docker container.

## Overview

The implementation packages the Spring PetClinic application as a GraalVM native image and deploys it in a minimal distroless Docker container. This approach provides several benefits:

- Faster startup time
- Lower memory footprint
- Smaller container size
- Improved security with minimal attack surface
- Better resource utilization

## Prerequisites

- GraalVM 21+ (for local native image builds)
- Docker
- Maven
- Git
- (Optional) Kubernetes cluster for deployment
- (Optional) GitHub account for container registry

## Building the Native Image

### Option 1: Build Locally

To build the native image locally:

```bash
# Make the script executable
chmod +x build-native-image.sh

# Build the native image
./build-native-image.sh
```

This will create a native executable at `target/spring-petclinic`.

### Option 2: Build with Docker

To build the native image using Docker:

```bash
# Make the script executable
chmod +x build-docker-image.sh

# Build the Docker image
./build-docker-image.sh
```

This will create a Docker image named `spring-petclinic-native:latest`.

## Running the Application

### Option 1: Run the Native Executable Locally

If you built the native image locally:

```bash
# Run the native executable
./target/spring-petclinic
```

### Option 2: Run the Docker Container

If you built the Docker image:

```bash
# Make the script executable
chmod +x run-container.sh

# Run the container
./run-container.sh
```

The application will be available at http://localhost:8080.

## Testing the Application

To test the native image container:

```bash
# Make the script executable
chmod +x test-native-container.sh

# Run the tests
./test-native-container.sh
```

This script will:
1. Start the container
2. Wait for the application to be ready
3. Test key endpoints
4. Check memory usage
5. Clean up the container

## Deploying to GitHub Container Registry and Kubernetes

To push the image to GitHub Container Registry and create a Kubernetes deployment:

```bash
# Make the script executable
chmod +x push-to-ghcr.sh

# Push to GHCR and create Kubernetes deployment YAML
./push-to-ghcr.sh <your_github_username>
```

This will:
1. Tag and push the image to GitHub Container Registry
2. Create a Kubernetes deployment YAML file

To deploy to Kubernetes:

```bash
kubectl apply -f kubernetes-deployment.yaml
```

## Implementation Details

### Native Image Configuration

The native image is configured in the `pom.xml` file with the following options:
- `--no-fallback`: Ensures a fully static native image
- `--initialize-at-build-time=org.h2.Driver`: Initializes the H2 database driver at build time
- `-H:+ReportExceptionStackTraces`: Provides detailed stack traces for debugging
- `-H:+PrintClassInitialization`: Helps with debugging class initialization issues
- `--enable-https` and `--enable-http`: Enables HTTP and HTTPS support
- `--enable-all-security-services`: Enables security services for HTTPS
- `--verbose`: Provides detailed build information

### Runtime Hints

Runtime hints are provided in `PetClinicRuntimeHints.java` for:
- Resources (static files, templates, properties)
- Serialization (all entity classes)
- Reflection (all entity classes with appropriate member categories)

### Docker Container

The Docker container uses:
- Multi-stage build for smaller image size
- Distroless base image for minimal attack surface
- Non-root user for security
- Proper port exposure
- Minimal included files

## Performance Considerations

The native image container provides:
- Faster startup time (typically under 1 second)
- Lower memory footprint (typically 50-70% less than JVM)
- Smaller container size
- Better CPU utilization

## Security Considerations

The implementation follows security best practices:
- Minimal base image (distroless)
- Non-root user
- Minimal included files and dependencies
- No shell or debugging tools in the production container
