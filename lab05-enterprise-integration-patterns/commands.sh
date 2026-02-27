#!/bin/bash
# Lab 05 - Implementing Enterprise Integration Patterns (EIPs)
# Commands Executed During Lab (Sequential)

# -------------------------------
# Task 1.1 - Create Project Structure
# -------------------------------
mkdir ~/eip-lab
cd ~/eip-lab

mvn archetype:generate -DgroupId=com.alnafi.eip \
  -DartifactId=eip-patterns-lab \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

cd eip-patterns-lab

# -------------------------------
# Task 1.1 - Update pom.xml (initial)
# -------------------------------
nano pom.xml

# Realistic fix: add Spring Boot parent + cleanup explicit Spring Boot starter versions
nano pom.xml

# -------------------------------
# Task 1.2 - Create Packages / Folders
# -------------------------------
mkdir -p src/main/java/com/alnafi/eip/model
mkdir -p src/main/java/com/alnafi/eip/routes
mkdir -p src/main/resources

# -------------------------------
# Task 1.2 - Create CustomerOrder Model
# -------------------------------
nano src/main/java/com/alnafi/eip/model/CustomerOrder.java

# -------------------------------
# Task 1.3 - Implement Content-Based Router Route
# -------------------------------
nano src/main/java/com/alnafi/eip/routes/ContentBasedRouterRoute.java

# -------------------------------
# Task 1.4 - Create REST Controller for Testing
# -------------------------------
mkdir -p src/main/java/com/alnafi/eip/controller
nano src/main/java/com/alnafi/eip/controller/OrderController.java

# -------------------------------
# Task 2.1 - Create Batch Order Model
# -------------------------------
nano src/main/java/com/alnafi/eip/model/BatchOrder.java

# -------------------------------
# Task 2.2 - Implement Splitter Route
# -------------------------------
nano src/main/java/com/alnafi/eip/routes/SplitterRoute.java

# -------------------------------
# Task 2.3 - Create Batch Order Controller
# -------------------------------
nano src/main/java/com/alnafi/eip/controller/BatchOrderController.java

# -------------------------------
# Task 2.4 - Create Main Spring Boot Application
# -------------------------------
nano src/main/java/com/alnafi/eip/EipPatternsApplication.java

# Extra realism cleanup (template check)
ls -la src/main/java/com/alnafi/eip

# -------------------------------
# Task 2.5 - Configure Application Properties (YAML)
# -------------------------------
nano src/main/resources/application.yml

# -------------------------------
# Task 3.1 - Build and Run
# -------------------------------
mvn clean compile
mvn spring-boot:run

# -------------------------------
# Task 3.2 - Test Content-Based Router (new terminal)
# -------------------------------
curl -X POST http://localhost:8080/api/orders/process \
  -H "Content-Type: application/json" \
  -d '{
  "orderId": "TEST-001",
  "customerType": "PREMIUM",
  "amount": 1500.0,
  "priority": "NORMAL",
  "productCategory": "ELECTRONICS"
  }'

curl -X POST http://localhost:8080/api/orders/process \
  -H "Content-Type: application/json" \
  -d '{
  "orderId": "TEST-002",
  "customerType": "STANDARD",
  "amount": 300.0,
  "priority": "HIGH",
  "productCategory": "CLOTHING"
  }'

curl -X POST http://localhost:8080/api/orders/process \
  -H "Content-Type: application/json" \
  -d '{
  "orderId": "TEST-003",
  "customerType": "STANDARD",
  "amount": 500.0,
  "priority": "NORMAL",
  "productCategory": "ELECTRONICS"
  }'

curl -X POST http://localhost:8080/api/orders/process \
  -H "Content-Type: application/json" \
  -d '{
  "orderId": "TEST-004",
  "customerType": "STANDARD",
  "amount": 100.0,
  "priority": "NORMAL",
  "productCategory": "BOOKS"
  }'

# -------------------------------
# Task 3.3 - Test Splitter Pattern
# -------------------------------
curl -X GET http://localhost:8080/api/batch/sample

curl -X POST http://localhost:8080/api/batch/process \
  -H "Content-Type: application/json" \
  -d '{
  "batchId": "BATCH-TEST-001",
  "orders": [
  {
  "orderId": "BATCH-ORD-001",
  "customerType": "PREMIUM",
  "amount": 1200.0,
  "priority": "HIGH",
  "productCategory": "ELECTRONICS"
  },
  {
  "orderId": "BATCH-ORD-002",
  "customerType": "STANDARD",
  "amount": 300.0,
  "priority": "NORMAL",
  "productCategory": "CLOTHING"
  },
  {
  "orderId": "BATCH-ORD-003",
  "customerType": "PREMIUM",
  "amount": 800.0,
  "priority": "NORMAL",
  "productCategory": "BOOKS"
  }
  ],
  "totalOrders": 3
  }'

curl -X POST http://localhost:8080/api/batch/process-with-aggregation \
  -H "Content-Type: application/json" \
  -d '{
  "batchId": "BATCH-AGG-001",
  "orders": [
  {
  "orderId": "AGG-ORD-001",
  "customerType": "PREMIUM",
  "amount": 1500.0,
  "priority": "HIGH",
  "productCategory": "ELECTRONICS"
  },
  {
  "orderId": "AGG-ORD-002",
  "customerType": "STANDARD",
  "amount": 250.0,
  "priority": "NORMAL",
  "productCategory": "CLOTHING"
  }
  ],
  "totalOrders": 2
  }'

# -------------------------------
# Troubleshooting Commands
# -------------------------------
sudo netstat -tulpn | grep 8080
sudo kill -9 4123

mvn dependency:resolve
mvn clean install

# -------------------------------
# Performance Testing Script
# -------------------------------
cd ~/eip-lab/eip-patterns-lab
nano test-performance.sh
chmod +x test-performance.sh
./test-performance.sh
