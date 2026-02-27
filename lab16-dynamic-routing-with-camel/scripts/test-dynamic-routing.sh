#!/bin/bash
echo "=== Testing Dynamic Routing with Camel ==="
echo

# Wait for application to start
sleep 5

echo "1. Testing Basic Priority Routing..."
echo " - High Priority Order:"
curl -s -X POST http://localhost:8080/orders \
 -H "Content-Type: application/json" \
 -d @test-data/high-priority-order.json
echo
echo

echo " - Medium Priority Order:"
curl -s -X POST http://localhost:8080/orders \
 -H "Content-Type: application/json" \
 -d @test-data/medium-priority-order.json
echo
echo

echo " - Low Priority Order:"
curl -s -X POST http://localhost:8080/orders \
 -H "Content-Type: application/json" \
 -d @test-data/low-priority-order.json
echo
echo

echo "2. Testing Category-based Routing..."
echo " - Electronics Product:"
curl -s -X POST http://localhost:8080/products \
 -H "Content-Type: application/json" \
 -d '{"productId": "PROD001", "category": "ELECTRONICS", "name": "Laptop"}'
echo
echo

echo " - Clothing Product:"
curl -s -X POST http://localhost:8080/products \
 -H "Content-Type: application/json" \
 -d '{"productId": "PROD002", "category": "CLOTHING", "name": "T-Shirt"}'
echo
echo

echo " - Books Product:"
curl -s -X POST http://localhost:8080/products \
 -H "Content-Type: application/json" \
 -d '{"productId": "PROD003", "category": "BOOKS", "name": "Programming Guide"}'
echo
echo

echo "3. Testing Complex Multi-criteria Routing..."
echo " - High Value Order:"
curl -s -X POST http://localhost:8080/complex-orders \
 -H "Content-Type: application/json" \
 -d @test-data/high-value-order.json
echo
echo

echo " - Standard Order:"
curl -s -X POST http://localhost:8080/complex-orders \
 -H "Content-Type: application/json" \
 -d @test-data/medium-priority-order.json
echo
echo

echo "4. Testing Customer Type Routing..."
echo " - Premium Customer:"
curl -s -X POST http://localhost:8080/customers \
 -H "Content-Type: application/json" \
 -d '{"customerId": "CUST001", "customerType": "PREMIUM", "name": "John Doe"}'
echo
echo

echo " - Standard Customer:"
curl -s -X POST http://localhost:8080/customers \
 -H "Content-Type: application/json" \
 -d '{"customerId": "CUST002", "customerType": "STANDARD", "name": "Jane Smith"}'
echo
echo

echo "5. Testing Time-sensitive Routing..."
curl -s -X POST http://localhost:8080/time-sensitive \
 -H "Content-Type: application/json" \
 -d '{"requestId": "REQ001", "type": "URGENT", "timestamp": "'$(date -Iseconds)'"}'
echo
echo

echo "=== All tests completed ==="
