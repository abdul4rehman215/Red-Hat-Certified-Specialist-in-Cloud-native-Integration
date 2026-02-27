#!/bin/bash
echo "=== Running Camel Route Tests ==="
echo "Cleaning and compiling project..."
mvn clean compile
echo ""
echo "Running unit tests..."
mvn test -Dtest=OrderProcessingRouteTest
echo ""
echo "Running integration tests..."
mvn test -Dtest=OrderProcessingIntegrationTest
echo ""
echo "Generating test report..."
mvn surefire-report:report
echo ""
echo "Test execution completed!"
echo "Check target/surefire-reports/ for detailed test results"
