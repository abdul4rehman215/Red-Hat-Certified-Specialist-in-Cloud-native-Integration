#!/bin/bash
# Lab 15 - Implementing API Gateway Pattern with Camel (Spring Boot + Camel)
# Commands Executed During Lab (Sequential, Copy-Paste Ready)

# ------------------------------------------------------------
# Task 1.1: Set Up Project Structure
# ------------------------------------------------------------

cd ~

mkdir camel-api-gateway
cd camel-api-gateway

mkdir -p src/main/java/com/example/gateway
mkdir -p src/main/resources
mkdir -p src/test/java

# ------------------------------------------------------------
# Task 1.2: Maven Configuration
# ------------------------------------------------------------

nano pom.xml

# ------------------------------------------------------------
# Task 1.3: Create Mock Backend Services (Camel Jetty)
# ------------------------------------------------------------

nano src/main/java/com/example/gateway/MockBackendRoutes.java

# ------------------------------------------------------------
# Task 1.4: Create API Gateway Routes
# ------------------------------------------------------------

nano src/main/java/com/example/gateway/ApiGatewayRoutes.java

# ------------------------------------------------------------
# Task 1.5: Spring Boot Main Class
# ------------------------------------------------------------

nano src/main/java/com/example/gateway/ApiGatewayApplication.java

# ------------------------------------------------------------
# Task 2.1: Configure Basic Authentication / Authorization
# ------------------------------------------------------------

nano src/main/java/com/example/gateway/SecurityConfig.java

# ------------------------------------------------------------
# Task 2.2: Implement Rate Limiting (Camel throttle)
# ------------------------------------------------------------

nano src/main/java/com/example/gateway/RateLimitingRoutes.java

# ------------------------------------------------------------
# Task 2.3: Implement Response Aggregation (Dashboard)
# ------------------------------------------------------------

nano src/main/java/com/example/gateway/AggregationRoutes.java

# ------------------------------------------------------------
# Task 2.4: Application Properties
# ------------------------------------------------------------

nano src/main/resources/application.properties

# ------------------------------------------------------------
# Task 3.1: Build and Run the Application
# ------------------------------------------------------------

mvn clean compile

# Start the app (kept running in terminal)
mvn spring-boot:run

# ------------------------------------------------------------
# Task 3.2: Basic Functionality Testing (New Terminal)
# ------------------------------------------------------------

curl -u user:password http://localhost:8080/api/v1/users
curl -u user:password http://localhost:8080/api/v1/products
curl -u admin:admin http://localhost:8080/api/v1/orders
curl -u admin:admin http://localhost:8080/api/v1/dashboard

# ------------------------------------------------------------
# Task 3.3: Rate Limiting Test Script
# ------------------------------------------------------------

nano test-rate-limiting.sh
chmod +x test-rate-limiting.sh
./test-rate-limiting.sh

# ------------------------------------------------------------
# Task 3.4: Performance Testing Script (Concurrent Requests)
# ------------------------------------------------------------

nano performance-test.sh
chmod +x performance-test.sh
./performance-test.sh

# ------------------------------------------------------------
# Task 3.5: Monitor Actuator Metrics
# ------------------------------------------------------------

curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/info
curl http://localhost:8080/actuator/metrics

# ------------------------------------------------------------
# Task 3.6: Simulated Failure Testing (Stopping JVM)
# ------------------------------------------------------------

ps aux | grep java
kill 23117

curl -u user:password http://localhost:8080/api/v1/users

# Restart app after failure simulation
cd ~/camel-api-gateway
mvn spring-boot:run

# ------------------------------------------------------------
# Task 3.7: Security Testing (AuthZ/AuthN)
# ------------------------------------------------------------

curl http://localhost:8080/api/v1/users
curl -u wronguser:wrongpass http://localhost:8080/api/v1/users
curl -u user:password http://localhost:8080/api/v1/orders
curl -u admin:admin http://localhost:8080/api/v1/orders

# ------------------------------------------------------------
# Troubleshooting: Port Already in Use
# ------------------------------------------------------------

sudo netstat -tulpn | grep :8080

# ------------------------------------------------------------
# Troubleshooting: Java Version / JAVA_HOME
# ------------------------------------------------------------

java -version
echo $JAVA_HOME
