#!/bin/bash
# Lab 12 - Consuming External REST APIs with Apache Camel
# Commands Executed During Lab (Sequential, Copy-Paste Ready)

# ------------------------------------------------------------
# Task 1: Create Maven Project Structure
# ------------------------------------------------------------

cd ~

mkdir camel-rest-consumer
cd camel-rest-consumer

mkdir -p src/main/java/com/example/camel
mkdir -p src/main/resources
mkdir -p src/test/java

nano pom.xml

# ------------------------------------------------------------
# Task 1: Create Basic REST Consumer Application + Route
# ------------------------------------------------------------

nano src/main/java/com/example/camel/RestConsumerApplication.java
nano src/main/java/com/example/camel/RestApiConsumerRoute.java

# Compile the project
mvn clean compile

# Run the application (basic route)
mvn exec:java

# ------------------------------------------------------------
# Task 2: Create Data Transformation Processor + Enhanced Routes
# ------------------------------------------------------------

nano src/main/java/com/example/camel/DataTransformProcessor.java
nano src/main/java/com/example/camel/EnhancedRestConsumerRoute.java

# Update main application to use enhanced route
nano src/main/java/com/example/camel/RestConsumerApplication.java

# ------------------------------------------------------------
# Task 3: Logging Configuration
# ------------------------------------------------------------

nano src/main/resources/simplelogger.properties

# ------------------------------------------------------------
# Task 3: Test Runner Script
# ------------------------------------------------------------

nano test-runner.sh
chmod +x test-runner.sh

# Build project again (after adding new files)
mvn clean compile

# Run test runner
./test-runner.sh

# ------------------------------------------------------------
# Task 3.4: Manual API Testing (curl + jq)
# ------------------------------------------------------------

echo "Testing Posts API..."
curl -X GET "https://jsonplaceholder.typicode.com/posts?_limit=3" \
 -H "Accept: application/json" \
 -H "User-Agent: Camel-REST-Consumer/1.0" | jq '.'

echo ""
echo "Testing Users API..."
curl -X GET "https://jsonplaceholder.typicode.com/users?_limit=2" \
 -H "Accept: application/json" \
 -H "User-Agent: Camel-REST-Consumer/1.0" | jq '.'

echo ""
echo "Testing Single User API..."
curl -X GET "https://jsonplaceholder.typicode.com/users/1" \
 -H "Accept: application/json" \
 -H "User-Agent: Camel-REST-Consumer/1.0" | jq '.'

# ------------------------------------------------------------
# Task 3.5: Monitoring and Debugging Route
# ------------------------------------------------------------

nano src/main/java/com/example/camel/MonitoringRoute.java

# ------------------------------------------------------------
# Task 3.6: Complete Test Application
# ------------------------------------------------------------

nano src/main/java/com/example/camel/CompleteTestApplication.java

# Compile everything
mvn clean compile

# Run complete test app (explicit main class)
mvn exec:java -Dexec.mainClass="com.example.camel.CompleteTestApplication"

# ------------------------------------------------------------
# Troubleshooting / Verification Commands
# ------------------------------------------------------------

# Test connectivity to external API
ping -c 3 jsonplaceholder.typicode.com

# Check API accessibility + response headers
curl -I https://jsonplaceholder.typicode.com/posts/1

# Resolve Maven dependencies
mvn dependency:resolve

# Increase memory for Maven/Java execution if needed
export MAVEN_OPTS="-Xmx512m -Xms256m"
