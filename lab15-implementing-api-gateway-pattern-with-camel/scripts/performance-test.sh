#!/bin/bash
# scripts/performance-test.sh

echo "Performance Testing - Concurrent Requests"
echo "========================================"

# Function to make concurrent requests
make_requests() {
  local endpoint=$1
  local auth=$2
  local count=$3

  echo "Testing $endpoint with $count concurrent requests"

  for i in $(seq 1 $count); do
    (
      start_time=$(date +%s%N)
      response=$(curl -s -w "%{http_code}" -u $auth http://localhost:8080$endpoint)
      end_time=$(date +%s%N)
      duration=$(( (end_time - start_time) / 1000000 ))
      echo "Request $i: HTTP $response, Duration: ${duration}ms"
    ) &
  done

  wait
  echo "Completed $count requests to $endpoint"
  echo "---"
}

# Test different endpoints with concurrent requests
make_requests "/api/v1/users" "user:password" 5
make_requests "/api/v1/products" "user:password" 5
make_requests "/api/v1/orders" "admin:admin" 3
make_requests "/api/v1/dashboard" "admin:admin" 2

echo "Performance testing completed!"
