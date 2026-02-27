#!/bin/bash
# Lab 06 - Testing Camel Routes with JUnit
# Commands Executed During Lab (Sequential / No Explanations)

mkdir camel-testing-lab
cd camel-testing-lab

mkdir -p src/main/java/com/example/camel
mkdir -p src/test/java/com/example/camel
mkdir -p src/main/resources
mkdir -p src/test/resources

nano pom.xml

touch src/main/java/com/example/camel/OrderProcessingRoute.java
nano src/main/java/com/example/camel/OrderProcessingRoute.java

touch src/main/java/com/example/camel/OrderProcessor.java
nano src/main/java/com/example/camel/OrderProcessor.java

touch src/test/java/com/example/camel/OrderProcessingRouteTest.java
nano src/test/java/com/example/camel/OrderProcessingRouteTest.java

nano src/test/java/com/example/camel/OrderProcessingRouteTest.java
nano src/test/java/com/example/camel/OrderProcessingRouteTest.java
nano src/test/java/com/example/camel/OrderProcessingRouteTest.java
nano src/test/java/com/example/camel/OrderProcessingRouteTest.java
nano src/test/java/com/example/camel/OrderProcessingRouteTest.java

touch src/test/java/com/example/camel/OrderProcessingIntegrationTest.java
nano src/test/java/com/example/camel/OrderProcessingIntegrationTest.java

mvn clean compile test

mvn test -Dtest=OrderProcessingRouteTest
mvn test -Dtest=OrderProcessingIntegrationTest

mvn test -X

mvn surefire-report:report
ls -la target/site

touch run-tests.sh
chmod +x run-tests.sh
nano run-tests.sh

./run-tests.sh
