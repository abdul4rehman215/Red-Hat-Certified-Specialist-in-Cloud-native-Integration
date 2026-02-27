#!/bin/bash
# Lab 10 - Configuring Distributed Transactions in Apache Camel
# Commands Executed During Lab (Sequential / No Explanations)

cd /home/student/workspace

mkdir -p /home/toor/workspace
cd /home/toor/workspace
pwd

mkdir camel-distributed-transactions
cd camel-distributed-transactions

mkdir -p src/main/java/com/alnafi/camel/transactions
mkdir -p src/main/resources
mkdir -p src/test/java

nano pom.xml

nano src/main/resources/application.yml

nano src/main/java/com/alnafi/camel/transactions/CamelTransactionApplication.java

mkdir -p src/main/java/com/alnafi/camel/transactions/config
nano src/main/java/com/alnafi/camel/transactions/config/TransactionConfig.java

nano src/main/resources/schema.sql

mkdir -p src/main/java/com/alnafi/camel/transactions/model
nano src/main/java/com/alnafi/camel/transactions/model/Order.java

mkdir -p src/main/java/com/alnafi/camel/transactions/routes
mkdir -p src/main/java/com/alnafi/camel/transactions/processor
nano src/main/java/com/alnafi/camel/transactions/routes/OrderProcessingRoute.java

nano src/main/java/com/alnafi/camel/transactions/processor/OrderProcessor.java
nano src/main/java/com/alnafi/camel/transactions/processor/InventoryProcessor.java

mkdir -p src/main/java/com/alnafi/camel/transactions/service
nano src/main/java/com/alnafi/camel/transactions/service/TestDataService.java

mkdir -p src/main/java/com/alnafi/camel/transactions/controller
nano src/main/java/com/alnafi/camel/transactions/controller/TestController.java

nano src/main/java/com/alnafi/camel/transactions/config/DatabaseInitializer.java

mvn clean package -DskipTests

java -jar target/camel-distributed-transactions-1.0.0.jar

curl -s -X POST http://localhost:8080/api/test/send-valid-order

curl -s http://localhost:8080/api/test/orders | head

curl -s http://localhost:8080/api/test/inventory | grep Laptop

curl -s -X POST http://localhost:8080/api/test/send-invalid-order

curl -s http://localhost:8080/api/test/orders

curl -s -X POST http://localhost:8080/api/test/send-insufficient-inventory-order

curl -s http://localhost:8080/api/test/inventory | grep Monitor

curl -s -X POST http://localhost:8080/api/test/send-invalid-data-order

curl -s http://localhost:8080/api/test/audit-log | head

curl -s -X POST http://localhost:8080/api/test/reset-data
