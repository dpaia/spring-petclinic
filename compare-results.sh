#!/bin/bash

# Script to compare the startup times of JVM and native image versions
# and document the tradeoffs

# Check if the startup-times.csv file exists
if [ ! -f "startup-times.csv" ]; then
  echo "Error: startup-times.csv file not found. Please run the measurement scripts first."
  exit 1
fi

# Read the startup times from the CSV file
JVM_TIME=$(grep "JVM" startup-times.csv | cut -d',' -f2)
NATIVE_TIME=$(grep "Native" startup-times.csv | cut -d',' -f2)

# Check if both times are available
if [ -z "$JVM_TIME" ] || [ -z "$NATIVE_TIME" ]; then
  echo "Error: Missing startup times. Please run both measurement scripts."
  exit 1
fi

# Calculate the speedup factor
SPEEDUP=$(echo "scale=2; $JVM_TIME / $NATIVE_TIME" | bc)

echo "==================================================="
echo "           STARTUP TIME COMPARISON RESULTS         "
echo "==================================================="
echo "JVM startup time:        $JVM_TIME seconds"
echo "Native image startup time: $NATIVE_TIME seconds"
echo "Speedup factor:          ${SPEEDUP}x"
echo "==================================================="

# Create a detailed report
cat > startup-comparison-report.md << EOF
# Spring PetClinic Startup Performance Comparison

## Measurement Results

| Runtime | Average Startup Time (seconds) |
|---------|-------------------------------|
| JVM     | $JVM_TIME                     |
| Native  | $NATIVE_TIME                  |

**Speedup factor: ${SPEEDUP}x**

## Tradeoffs Analysis

### JVM Version

**Advantages:**
- Dynamic optimization at runtime
- No need for ahead-of-time compilation
- Full runtime reflection capabilities
- Easier debugging and profiling
- Smaller build time

**Disadvantages:**
- Slower startup time
- Higher initial memory usage
- Warm-up period required for optimal performance

### Native Image Version

**Advantages:**
- Significantly faster startup time (${SPEEDUP}x in our tests)
- Lower initial memory footprint
- No warm-up period needed
- Better performance in serverless/container environments
- Reduced CPU usage during startup

**Disadvantages:**
- Longer build time
- Limited reflection capabilities (requires explicit configuration)
- More complex debugging
- Less runtime optimization
- May have compatibility issues with some libraries

## Conclusion

The GraalVM native image provides a significant improvement in startup time compared to the traditional JVM, making it well-suited for microservices, serverless functions, and containerized applications where fast startup is critical. However, it comes with tradeoffs in terms of build complexity, debugging difficulty, and runtime flexibility.

For the Spring PetClinic application, which is a relatively small application with a REST API and database access, the native image provides a compelling advantage in terms of startup performance. In production environments where the application needs to scale quickly or restart frequently, this performance improvement could be valuable.

However, for development environments or applications that run for long periods without restarts, the traditional JVM might still be preferable due to its better tooling support and runtime optimization capabilities.
EOF

echo "Detailed comparison report created: startup-comparison-report.md"
