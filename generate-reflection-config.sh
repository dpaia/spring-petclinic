#!/bin/bash
# Script to generate GraalVM reflection configuration using native-image-agent

# Ensure script fails on error
set -e

echo "Starting GraalVM native-image-agent configuration generation"

# Check if JAVA_HOME is set and points to a GraalVM installation
if [ -z "$JAVA_HOME" ] || [ ! -f "$JAVA_HOME/bin/java" ]; then
  echo "Error: JAVA_HOME is not set or does not point to a valid Java installation"
  echo "Please set JAVA_HOME to point to a GraalVM installation"
  exit 1
fi

# Check if the java version is GraalVM
if ! "$JAVA_HOME/bin/java" -version 2>&1 | grep -q "GraalVM"; then
  echo "Warning: Your Java installation does not appear to be GraalVM"
  echo "The native-image-agent may not be available"
fi

# Create output directory for agent-generated configs
mkdir -p target/native-image-configs

echo "Building the application..."
# Build the application
./mvnw clean package -DskipTests

echo "Running tests with native-image-agent to capture reflection usage..."
# Run the application with the native-image-agent to capture reflection usage
"$JAVA_HOME/bin/java" \
  -agentlib:native-image-agent=config-output-dir=target/native-image-configs \
  -jar target/spring-petclinic-*.jar \
  --spring.profiles.active=default \
  --server.port=0 &

# Store the PID of the application
APP_PID=$!

echo "Application started with PID: $APP_PID"
echo "Waiting for application to initialize..."
sleep 10

echo "Running tests to exercise reflection code paths..."
# Run tests to exercise the reflection code paths
./mvnw test -Dtest=EntityReflectionUtilTests

echo "Shutting down the application..."
# Shutdown the application gracefully
kill $APP_PID
sleep 5

echo "Copying generated configuration files to resources directory..."
# Copy the generated configuration files to the resources directory
mkdir -p src/main/resources/META-INF/native-image
cp target/native-image-configs/reflect-config.json src/main/resources/META-INF/native-image/
cp target/native-image-configs/resource-config.json src/main/resources/META-INF/native-image/
cp target/native-image-configs/proxy-config.json src/main/resources/META-INF/native-image/
cp target/native-image-configs/jni-config.json src/main/resources/META-INF/native-image/

echo "Configuration generation complete!"
echo "Generated files are in src/main/resources/META-INF/native-image/"
echo ""
echo "To build a native image with these configurations, run:"
echo "./mvnw -Pnative native:compile"
