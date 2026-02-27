#!/bin/bash
# Lab 02 - Creating Simple Camel Routes Using Java DSL
# Commands Executed During Lab (Sequential)

# -------------------------------
# Task 1.1 - Set Up Project Structure
# -------------------------------
mkdir camel-java-dsl-lab
cd camel-java-dsl-lab

mvn archetype:generate \
  -DgroupId=com.alnafi.camel \
  -DartifactId=simple-routes \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

cd simple-routes

# -------------------------------
# Task 1.2 - Configure Maven Dependencies
# -------------------------------
nano pom.xml
head -n 10 pom.xml

# -------------------------------
# Task 1.3 - Create Route Builder (Java DSL)
# -------------------------------
mkdir -p src/main/java/com/alnafi/camel
nano src/main/java/com/alnafi/camel/SimpleRouteBuilder.java
sed -n '1,25p' src/main/java/com/alnafi/camel/SimpleRouteBuilder.java

# -------------------------------
# Task 1.4 - Create Main Application Class
# -------------------------------
nano src/main/java/com/alnafi/camel/CamelApplication.java

# -------------------------------
# Task 1.5 - Create Required Directories
# -------------------------------
mkdir -p input output processed simple-output
ls -la

# -------------------------------
# Task 2.1 - Build the Project
# -------------------------------
mvn clean compile
echo "Build Status: $?"

# -------------------------------
# Task 2.2 - Run the Application
# -------------------------------
mvn exec:java -Dexec.mainClass="com.alnafi.camel.CamelApplication"

# -------------------------------
# Task 2.3 - Test File-to-File Route (New Terminal)
# -------------------------------
cd camel-java-dsl-lab/simple-routes

nano input/test-message.txt
nano input/order-data.txt

ls -la input/

# -------------------------------
# Task 2.4 - Verify Route Processing
# -------------------------------
ls -la output/
ls -la processed/
ls -la simple-output/

cat output/processed-message-*.txt
cat processed/processed-test-message.txt
cat simple-output/simple-*.txt

watch -n 2 'ls -la output/ processed/ simple-output/'

# -------------------------------
# Task 2.5 - Test Different Message Types
# -------------------------------
nano input/json-message.json
nano input/customer-data.csv

# (Observed output in application terminal)

# -------------------------------
# Task 2.6 - Stop and Restart Testing
# -------------------------------
# Stop application: Ctrl+C

rm -f output/* processed/* simple-output/*
ls -la output/ processed/ simple-output/

mvn exec:java -Dexec.mainClass="com.alnafi.camel.CamelApplication"

# -------------------------------
# Advanced Testing - Troubleshooting Checks
# -------------------------------
ls -la input/
chmod 644 input/*
java -version
mvn dependency:tree
ls -la
mkdir -p output processed simple-output

# -------------------------------
# Monitoring and Debugging (Enable Debug Logging)
# -------------------------------
mkdir -p src/main/resources
nano src/main/resources/simplelogger.properties

# -------------------------------
# Optional Enhancement - Create Enhanced Route Builder
# -------------------------------
nano src/main/java/com/alnafi/camel/EnhancedRouteBuilder.java
