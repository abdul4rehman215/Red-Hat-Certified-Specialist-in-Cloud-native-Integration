#!/bin/bash
# Lab 03 - Defining Routes with XML DSL
# Commands Executed During Lab (Sequential)

# -------------------------------
# Task 1.1 - Create Maven Project
# -------------------------------
cd ~

mvn archetype:generate -DgroupId=com.alnafi.camel.xmldsl \
  -DartifactId=camel-xml-dsl-lab \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

cd camel-xml-dsl-lab

# -------------------------------
# Task 1.2 - Configure Maven Dependencies
# -------------------------------
nano pom.xml
mvn clean compile

# -------------------------------
# Task 1.3 - Create Directory Structure
# -------------------------------
mkdir -p src/main/resources
mkdir -p src/main/resources/camel
mkdir -p src/test/java/com/alnafi/camel/xmldsl
mkdir -p data/input
mkdir -p data/output
mkdir -p data/processed

find data -maxdepth 2 -type d

# -------------------------------
# Task 2.1 - Create Main Application Class
# -------------------------------
mkdir -p src/main/java/com/alnafi/camel/xmldsl
nano src/main/java/com/alnafi/camel/xmldsl/CamelXmlDslApplication.java

# -------------------------------
# Task 2.2 - Create XML DSL Routes
# -------------------------------
nano src/main/resources/camel/file-processing-route.xml

# -------------------------------
# Task 2.3 - Configure Application Properties
# -------------------------------
nano src/main/resources/application.properties

# -------------------------------
# Task 3.1 - Create Content-Based Router Route
# -------------------------------
nano src/main/resources/camel/content-based-router.xml

# Fix dependency for jsonpath usage in XML routes
nano pom.xml
mvn clean compile

# -------------------------------
# Task 3.2 - Create Error Handling Route
# -------------------------------
nano src/main/resources/camel/error-handling-route.xml

# -------------------------------
# Task 4.1 - Create Test Data Files
# -------------------------------
echo "Hello World! This is a test file for XML DSL routing." > data/input/test1.txt
echo "Apache Camel makes integration easy and powerful." > data/input/test2.txt
echo "XML DSL provides declarative route configuration." > data/input/test3.txt

ls -la data/input/

# -------------------------------
# Task 4.2 - Create Unit Tests
# -------------------------------
nano src/test/java/com/alnafi/camel/xmldsl/XmlDslRouteTest.java

# -------------------------------
# Task 4.3 - Build and Run the Application
# -------------------------------
mvn clean compile
mvn spring-boot:run

# -------------------------------
# Task 4.3 - Monitor Outputs (New Terminal)
# -------------------------------
cd camel-xml-dsl-lab
watch -n 2 'find data/output -type f -name "*.txt" -o -name "*.json" | head -10'

# -------------------------------
# Task 4.4 - Verify Route Functionality
# -------------------------------
ls -la data/output/
cat data/output/*.txt

ls -la data/output/premium/
ls -la data/output/standard/
ls -la data/output/basic/

ls -la data/output/errors/
cat data/output/errors/*.txt

# -------------------------------
# Task 4.5 - Run Unit Tests
# -------------------------------
mvn test
mvn surefire-report:report

# -------------------------------
# Task 5.1 - Create Aggregation Route
# -------------------------------
nano src/main/resources/camel/aggregation-route.xml
mkdir -p data/output/aggregated

# -------------------------------
# Task 5.2 - Create Aggregation Strategy Bean
# -------------------------------
nano src/main/java/com/alnafi/camel/xmldsl/MyAggregationStrategy.java
mvn clean compile

# -------------------------------
# Task 6.1 - Enable JMX Monitoring
# -------------------------------
nano src/main/resources/application.properties

# -------------------------------
# Task 6.2 - Create Health Check Route
# -------------------------------
nano src/main/resources/camel/health-check-route.xml
mkdir -p data/output/health

# -------------------------------
# Troubleshooting / Validation Helpers
# -------------------------------
chmod 755 data/
chmod 755 data/output/

xmllint --noout src/main/resources/camel/*.xml
mvn dependency:tree
