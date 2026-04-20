#!/bin/bash

# Script to validate that the application endpoints used in profiling are working correctly
# This is an alternative to unit tests that might be difficult to run in certain environments

# Function to test an endpoint
test_endpoint() {
  local url=$1
  local expected_status=$2
  local description=$3
  
  echo "Testing $description at $url..."
  
  # Make the request and capture the status code
  status=$(curl -s -o /dev/null -w "%{http_code}" $url)
  
  if [ "$status" -eq "$expected_status" ]; then
    echo "✅ Success: $description returned status $status as expected"
    return 0
  else
    echo "❌ Error: $description returned status $status, expected $expected_status"
    return 1
  fi
}

# Function to test JSON response content
test_json_content() {
  local url=$1
  local json_path=$2
  local expected_value=$3
  local description=$4
  
  echo "Testing $description JSON content at $url..."
  
  # Make the request and check if the JSON path exists and has the expected value
  if command -v jq &> /dev/null; then
    # If jq is available, use it for proper JSON parsing
    result=$(curl -s $url | jq -r "$json_path")
    if [ "$result" = "$expected_value" ] || [ "$expected_value" = "*" -a -n "$result" ]; then
      echo "✅ Success: $description JSON content is valid"
      return 0
    else
      echo "❌ Error: $description JSON content is invalid. Got '$result', expected '$expected_value'"
      return 1
    fi
  else
    # Fallback to simple grep if jq is not available
    if curl -s $url | grep -q "vetList"; then
      echo "✅ Success: $description JSON content contains expected fields"
      return 0
    else
      echo "❌ Error: $description JSON content does not contain expected fields"
      return 1
    fi
  fi
}

# Function to test performance
test_performance() {
  local url=$1
  local num_requests=$2
  local max_avg_time=$3
  local description=$4
  
  echo "Testing $description performance with $num_requests requests..."
  
  # Make multiple requests and measure the time
  start_time=$(date +%s.%N)
  
  for i in $(seq 1 $num_requests); do
    curl -s $url > /dev/null
  done
  
  end_time=$(date +%s.%N)
  total_time=$(echo "$end_time - $start_time" | bc)
  avg_time=$(echo "scale=3; $total_time / $num_requests" | bc)
  
  echo "Average response time: $avg_time seconds per request"
  
  if (( $(echo "$avg_time <= $max_avg_time" | bc -l) )); then
    echo "✅ Success: $description performance is acceptable"
    return 0
  else
    echo "❌ Warning: $description performance is slower than expected"
    return 1
  fi
}

# Main validation function
validate_application() {
  local base_url="http://localhost:8080"
  local failures=0
  
  echo "Starting application validation..."
  
  # Test health endpoint
  if ! test_endpoint "$base_url/actuator/health" 200 "Health endpoint"; then
    failures=$((failures + 1))
  fi
  
  # Test vets endpoint
  if ! test_endpoint "$base_url/vets" 200 "Vets endpoint"; then
    failures=$((failures + 1))
  fi
  
  # Test vets JSON content
  if ! test_json_content "$base_url/vets" "." "*" "Vets endpoint"; then
    failures=$((failures + 1))
  fi
  
  # Test vets endpoint performance
  if ! test_performance "$base_url/vets" 10 0.5 "Vets endpoint"; then
    failures=$((failures + 1))
  fi
  
  echo "Validation completed with $failures failures."
  
  if [ $failures -eq 0 ]; then
    echo "✅ All validation tests passed! The application is ready for profiling."
    return 0
  else
    echo "❌ Some validation tests failed. Please check the application configuration."
    return 1
  fi
}

# Check if an application is already running
if curl -s http://localhost:8080/actuator/health > /dev/null; then
  echo "Application is already running. Starting validation..."
  validate_application
else
  echo "No application detected at http://localhost:8080."
  echo "Please start the application first using one of the following commands:"
  echo "  - For JVM version: java -jar target/spring-petclinic-*.jar"
  echo "  - For native image: ./target/spring-petclinic"
  exit 1
fi
