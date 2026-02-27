#!/bin/bash
# Lab 04 - Implementing Custom Processors in Camel Routes
# Commands Executed During Lab (Sequential)

# -------------------------------
# Task 1.1 - Set Up Maven Project
# -------------------------------
cd ~

mvn archetype:generate \
  -DgroupId=com.alnafi.camel.lab4 \
  -DartifactId=custom-processor-lab \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

cd custom-processor-lab

# -------------------------------
# Task 1.2 - Configure Maven Dependencies
# -------------------------------
nano pom.xml
grep -n "camel.version" -n pom.xml

# -------------------------------
# Task 1.3 - Create Custom Processor Class
# -------------------------------
mkdir -p src/main/java/com/alnafi/camel/lab4/processors
nano src/main/java/com/alnafi/camel/lab4/processors/MessageTransformProcessor.java

# -------------------------------
# Task 1.4 - Create Advanced Custom Processor
# -------------------------------
nano src/main/java/com/alnafi/camel/lab4/processors/MessageEnrichmentProcessor.java

# -------------------------------
# Task 2.1 - Create Route Builder with Custom Processors
# -------------------------------
mkdir -p src/main/java/com/alnafi/camel/lab4/routes
nano src/main/java/com/alnafi/camel/lab4/routes/CustomProcessorRouteBuilder.java

# -------------------------------
# Task 2.2 - Create Main Application Class
# -------------------------------
nano src/main/java/com/alnafi/camel/lab4/CustomProcessorApplication.java

# -------------------------------
# Task 2.3 - Build and Compile
# -------------------------------
mvn clean compile

# -------------------------------
# Task 3.1 - Run the Application
# -------------------------------
mvn exec:java -Dexec.mainClass="com.alnafi.camel.lab4.CustomProcessorApplication"

# -------------------------------
# Task 3.2 - Create Unit Test Class
# -------------------------------
mkdir -p src/test/java/com/alnafi/camel/lab4/processors
nano src/test/java/com/alnafi/camel/lab4/processors/MessageTransformProcessorTest.java

# -------------------------------
# Task 3.3 - Create Integration Test Class
# -------------------------------
mkdir -p src/test/java/com/alnafi/camel/lab4/routes
nano src/test/java/com/alnafi/camel/lab4/routes/CustomProcessorRouteTest.java

# -------------------------------
# Task 3.4 - Run Tests
# -------------------------------
mvn test
mvn test -Dtest=MessageTransformProcessorTest
mvn test -Dtest=CustomProcessorRouteTest

# -------------------------------
# Task 3.5 - Create Manual Testing Script
# -------------------------------
nano test-custom-processors.sh
chmod +x test-custom-processors.sh
./test-custom-processors.sh

# -------------------------------
# Task 3.6 - Advanced Testing Application
# -------------------------------
nano src/main/java/com/alnafi/camel/lab4/AdvancedTestApplication.java

mvn clean compile
mvn exec:java -Dexec.mainClass="com.alnafi.camel.lab4.AdvancedTestApplication"
