#!/bin/bash
# Lab 11 - Exposing RESTful APIs with Apache Camel (REST DSL)
# Commands Executed During Lab (Sequential, Copy-Paste Ready)

# ------------------------------------------------------------
# Task 1: Project Setup (Maven Structure)
# ------------------------------------------------------------

mkdir camel-rest-api
cd camel-rest-api

mkdir -p src/main/java/com/example/camel
mkdir -p src/main/resources

nano pom.xml

nano src/main/java/com/example/camel/User.java
nano src/main/java/com/example/camel/UserService.java

# ------------------------------------------------------------
# Task 2: Create Camel REST Route + Main Application
# ------------------------------------------------------------

nano src/main/java/com/example/camel/UserRestRoute.java
nano src/main/java/com/example/camel/CamelRestApplication.java

# Build the project
mvn clean compile

# Run the application (keep running in this terminal)
mvn exec:java -Dexec.mainClass="com.example.camel.CamelRestApplication"

# ------------------------------------------------------------
# Task 3: API Testing with curl (run in a SECOND terminal)
# ------------------------------------------------------------

# GET all users
curl -X GET http://localhost:8080/api/users \
 -H "Content-Type: application/json" \
 -w "\nHTTP Status: %{http_code}\n"

# GET user by ID (1)
curl -X GET http://localhost:8080/api/users/1 \
 -H "Content-Type: application/json" \
 -w "\nHTTP Status: %{http_code}\n"

# GET non-existent user (999)
curl -X GET http://localhost:8080/api/users/999 \
 -H "Content-Type: application/json" \
 -w "\nHTTP Status: %{http_code}\n"

# POST create new user
curl -X POST http://localhost:8080/api/users \
 -H "Content-Type: application/json" \
 -d '{
 "name": "Alice Cooper",
 "email": "alice.cooper@example.com",
 "age": 28
 }' \
 -w "\nHTTP Status: %{http_code}\n"

# POST invalid data (missing name/email)
curl -X POST http://localhost:8080/api/users \
 -H "Content-Type: application/json" \
 -d '{
 "age": 28
 }' \
 -w "\nHTTP Status: %{http_code}\n"

# PUT update user (ID 1)
curl -X PUT http://localhost:8080/api/users/1 \
 -H "Content-Type: application/json" \
 -d '{
 "name": "John Updated",
 "email": "john.updated@example.com",
 "age": 31
 }' \
 -w "\nHTTP Status: %{http_code}\n"

# PUT non-existent user (ID 999)
curl -X PUT http://localhost:8080/api/users/999 \
 -H "Content-Type: application/json" \
 -d '{
 "name": "Non Existent",
 "email": "nonexistent@example.com",
 "age": 25
 }' \
 -w "\nHTTP Status: %{http_code}\n"

# DELETE user (ID 2)
curl -X DELETE http://localhost:8080/api/users/2 \
 -w "\nHTTP Status: %{http_code}\n"

# DELETE non-existent user (ID 999)
curl -X DELETE http://localhost:8080/api/users/999 \
 -w "\nHTTP Status: %{http_code}\n"

# ------------------------------------------------------------
# Task 3.3: Automated Test Script
# ------------------------------------------------------------

nano test-api.sh
chmod +x test-api.sh

# First run (jq missing)
./test-api.sh

# Install jq (realistic troubleshooting step)
sudo apt-get update
sudo apt-get install -y jq

# Re-run test script after installing jq
./test-api.sh

# ------------------------------------------------------------
# Optional Troubleshooting / Monitoring Commands (Referenced)
# ------------------------------------------------------------

# Check if port 8080 is in use
sudo netstat -tulpn | grep :8080

# Kill a process if needed (replace <process_id>)
sudo kill -9 <process_id>

# Validate JSON formatting using jq
echo '{"name":"test"}' | jq '.'

# Verify Java version
java -version

# Inspect Maven dependency tree
mvn dependency:tree

# Install curl if missing
sudo apt-get update
sudo apt-get install -y curl

# Alternative POST using wget
wget -qO- --post-data='{"name":"test"}' --header='Content-Type:application/json' \
http://localhost:8080/api/users

# Monitor logs / connections / memory usage
tail -f camel-rest-api.log
netstat -an | grep :8080
ps aux | grep java
