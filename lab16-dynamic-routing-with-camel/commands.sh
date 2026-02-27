#!/bin/bash
# Lab 16 - Dynamic Routing with Camel
# Commands Executed During Lab (sequential, no explanations)

mkdir camel-dynamic-routing
cd camel-dynamic-routing

mkdir -p src/main/java/com/example/routing
mkdir -p src/main/resources
mkdir -p src/test/java

nano pom.xml

nano src/main/java/com/example/routing/DynamicRoutingExample.java

mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.routing.DynamicRoutingExample"

cd ~/camel-dynamic-routing

curl -X POST http://localhost:8080/orders \
 -H "Content-Type: application/json" \
 -d '{"orderId": "ORD001", "priority": "HIGH", "amount": 1000}'

curl -X POST http://localhost:8080/orders \
 -H "Content-Type: application/json" \
 -d '{"orderId": "ORD002", "priority": "MEDIUM", "amount": 500}'

curl -X POST http://localhost:8080/orders \
 -H "Content-Type: application/json" \
 -d '{"orderId": "ORD003", "priority": "LOW", "amount": 100}'

curl -X POST http://localhost:8080/customers \
 -H "Content-Type: application/json" \
 -d '{"customerId": "CUST001", "customerType": "PREMIUM", "name": "John Doe"}'

nano src/main/java/com/example/routing/ConfigurationService.java

nano src/main/java/com/example/routing/AdvancedDynamicRouting.java

nano src/main/resources/routing-config.properties

mkdir -p test-data

nano test-data/high-priority-order.json
nano test-data/medium-priority-order.json
nano test-data/low-priority-order.json
nano test-data/high-value-order.json

nano test-dynamic-routing.sh
chmod +x test-dynamic-routing.sh

# Stop running app
^C

mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.routing.AdvancedDynamicRouting"

./test-dynamic-routing.sh

nano src/main/java/com/example/routing/AdvancedDynamicRouting.java

# Restart advanced app
^C

mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.routing.AdvancedDynamicRouting"

./test-dynamic-routing.sh

nano src/main/java/com/example/routing/PerformanceTest.java

mvn exec:java -Dexec.mainClass="com.example.routing.PerformanceTest"

ps aux | grep java

sudo lsof -i :8080
sudo kill -9 <PID>

mvn -q -Dexec.mainClass="com.example.routing.AdvancedDynamicRouting" exec:java
