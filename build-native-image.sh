#!/bin/bash
set -e

echo "Building Spring PetClinic native image..."
./mvnw -Pnative native:compile -DskipTests

echo "Native image built successfully!"
echo "The native executable is located at: target/spring-petclinic"
echo "You can run it directly with: ./target/spring-petclinic"
