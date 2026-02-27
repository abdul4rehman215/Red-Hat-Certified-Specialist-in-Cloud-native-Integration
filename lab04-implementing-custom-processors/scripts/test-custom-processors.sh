#!/bin/bash

echo "=== Custom Processor Testing Script ==="
echo "This script will test various message scenarios"
echo

# Function to run a test case
run_test() {
  echo "Running test: $1"
  echo "Command: $2"
  eval $2
  echo "Test completed. Press Enter to continue..."
  read
  echo
}

# Build the project first
echo "Building the project..."
mvn clean compile -q
if [ $? -eq 0 ]; then
  echo "Build successful!"
  echo
else
  echo "Build failed. Please check for compilation errors."
  exit 1
fi

# Test 1: Run the main application
run_test "Main Application Test" \
 "timeout 30s mvn exec:java -Dexec.mainClass='com.alnafi.camel.lab4.CustomProcessorApplication' -q"

# Test 2: Run unit tests
run_test "Unit Tests" \
 "mvn test -Dtest=MessageTransformProcessorTest -q"

# Test 3: Run integration tests
run_test "Integration Tests" \
 "mvn test -Dtest=CustomProcessorRouteTest -q"

# Test 4: Run all tests
run_test "All Tests" \
 "mvn test -q"

echo "=== All tests completed ==="
echo "Check the output above for any failures or errors."
