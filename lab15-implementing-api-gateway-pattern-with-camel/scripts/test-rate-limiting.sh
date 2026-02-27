#!/bin/bash
# scripts/test-rate-limiting.sh

echo "Testing Rate Limiting for User Service (10 requests/minute limit)"
echo "=================================================="

for i in {1..15}; do
  echo "Request $i:"
  response=$(curl -s -w "HTTP Status: %{http_code}\n" -u user:password http://localhost:8080/api/v1/users)
  echo "$response"
  echo "---"
  sleep 2
done

echo "Testing Rate Limiting for Product Service (15 requests/minute limit)"
echo "====================================================="

for i in {1..20}; do
  echo "Request $i:"
  response=$(curl -s -w "HTTP Status: %{http_code}\n" -u user:password http://localhost:8080/api/v1/products)
  echo "$response"
  echo "---"
  sleep 1
done
