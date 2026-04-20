#!/bin/bash

# Script to measure the cold startup time of Spring PetClinic as a JVM JAR
# and test the /vets REST API endpoint

echo "Building Spring PetClinic as a JVM JAR..."
./mvnw clean package -DskipTests

# Get the path to the JAR file
JAR_FILE=$(find target -name "*.jar" -not -name "*sources*" -not -name "*javadoc*" | head -1)
echo "JAR file: $JAR_FILE"

# Function to measure startup time and test the API
measure_startup() {
  echo "Run $1: Measuring cold startup time..." >&2
  
  # Start the application and capture the startup time
  START_TIME=$(date +%s.%N)
  
  # Run the application in the background
  java -jar $JAR_FILE &>/dev/null </dev/null &
  PID=$!
  
  # Wait for the application to start by polling the health endpoint
  echo "Waiting for application to start..." >&2
  while ! curl -s http://localhost:8080/actuator/health | grep -q "UP"; do
    sleep 0.1
    # Check if the process is still running
    if ! ps -p $PID > /dev/null; then
      echo "Application failed to start" >&2
      exit 1
    fi
  done
  
  END_TIME=$(date +%s.%N)
  STARTUP_TIME=$(echo "$END_TIME - $START_TIME" | bc)
  echo "Startup time: $STARTUP_TIME seconds" >&2
  
  # Test the /vets REST API endpoint
  echo "Testing /vets REST API endpoint..." >&2
  curl -s http://localhost:8080/vets | head -20 >&2
  
  # Kill the application
  echo "Stopping application..." >&2
  kill $PID
  wait $PID 2>/dev/null
  
  # Return the startup time (only this will be captured by command substitution)
  echo $STARTUP_TIME
}

# Run the measurement multiple times and calculate the average
NUM_RUNS=3
TOTAL_TIME=0

for i in $(seq 1 $NUM_RUNS); do
  # Wait a bit between runs to ensure cold startup
  if [ $i -gt 1 ]; then
    echo "Waiting for 5 seconds before next run..."
    sleep 5
  fi
  
  # Measure startup time
  TIME=$(measure_startup $i)
  TOTAL_TIME=$(echo "${TOTAL_TIME} + ${TIME}" | bc)

  echo "-------------------8<---------------------"
  echo "Run ${i} completed. Startup time: ${TIME} seconds"
  echo "------------------->8---------------------"
done

# Calculate average
AVG_TIME=$(echo "scale=3; $TOTAL_TIME / $NUM_RUNS" | bc)
echo "Average JVM startup time over $NUM_RUNS runs: $AVG_TIME seconds"

# Save the result to a file for later comparison
echo "JVM,$AVG_TIME" > startup-times.csv
