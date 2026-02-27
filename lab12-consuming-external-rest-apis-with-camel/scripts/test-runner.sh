#!/bin/bash
# scripts/test-runner.sh
echo "=== Camel REST API Consumer Test Runner ==="
echo ""

# Function to run test
run_test() {
 echo "Starting test: $1"
 echo "Duration: $2 seconds"
 echo "----------------------------------------"

 timeout $2 mvn exec:java -Dexec.mainClass="com.example.camel.RestConsumerApplication" 2>&1 | tee test-output.log

 echo ""
 echo "Test completed. Check test-output.log for details."
 echo "========================================"
 echo ""
}

# Test 1: Basic functionality test
echo "Test 1: Basic API consumption test"
run_test "Basic API Test" 60

# Check if JSONPlaceholder API is accessible
echo "Verifying API accessibility..."
curl -s -o /dev/null -w "%{http_code}" https://jsonplaceholder.typicode.com/posts/1
echo ""

# Test 2: Extended test with more data
echo "Test 2: Extended API consumption test"
run_test "Extended API Test" 90

echo "All tests completed!"
echo "Check the logs above for detailed results."
