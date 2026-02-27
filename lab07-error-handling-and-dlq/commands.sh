#!/bin/bash
# Lab 07 - Error Handling and Dead Letter Queues (DLQ)
# Commands Executed During Lab (Sequential / No Explanations)

pwd

cd /home/student/camel-labs

mkdir -p /home/toor/camel-labs
cd /home/toor/camel-labs
pwd

mkdir lab7-error-handling
cd lab7-error-handling

mkdir -p src/main/java/com/alnafi/camel/errorhandling
mkdir -p src/main/resources
mkdir -p src/test/java

nano pom.xml

nano start-broker.sh
chmod +x start-broker.sh

nano src/main/java/com/alnafi/camel/errorhandling/ErrorSimulationService.java

nano src/main/java/com/alnafi/camel/errorhandling/ErrorHandlingRouteBuilder.java

nano src/main/java/com/alnafi/camel/errorhandling/RetryTestApplication.java

mvn clean compile
mvn exec:java -Dexec.mainClass="com.alnafi.camel.errorhandling.RetryTestApplication"

ls -la output/success/
ls -la output/validation-errors/
cat output/validation-errors/validation-error-20260227-152247.txt

nano src/main/java/com/alnafi/camel/errorhandling/JmsConfig.java

nano src/main/java/com/alnafi/camel/errorhandling/DLQRouteBuilder.java

nano src/main/java/com/alnafi/camel/errorhandling/DLQTestApplication.java

mvn clean compile
mvn exec:java -Dexec.mainClass="com.alnafi.camel.errorhandling.DLQTestApplication"

ls -R output/dlq | head

nano src/main/java/com/alnafi/camel/errorhandling/AdvancedErrorSimulation.java

nano src/main/java/com/alnafi/camel/errorhandling/ComprehensiveErrorRoutes.java

nano src/main/java/com/alnafi/camel/errorhandling/ComprehensiveDLQTestApplication.java

mvn clean compile
mvn exec:java -Dexec.mainClass="com.alnafi.camel.errorhandling.ComprehensiveDLQTestApplication"

ls -la output/success/ | tail
find output/dlq -maxdepth 2 -type d
ls -la output/dlq | grep dlq-stats | tail -n 3
cat output/dlq/dlq-stats-20260227-153640.txt
