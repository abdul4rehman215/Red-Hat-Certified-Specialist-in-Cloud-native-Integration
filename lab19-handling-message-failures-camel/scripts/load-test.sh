#!/bin/bash
echo "Starting load test for Camel error handling..."

# Generate messages for retry strategy
for i in {1..10}; do
 echo "Retry load test message $i - $(date)" > input/retry/load-test-retry-$i.txt
 sleep 0.5
done

# Generate messages for DLQ strategy
for i in {1..10}; do
 echo "DLQ load test message $i - $(date)" > input/dlq/load-test-dlq-$i.txt
 sleep 0.5
done

# Generate messages for fallback strategy
for i in {1..10}; do
 echo "Fallback load test message $i - $(date)" > input/fallback/load-test-fallback-$i.txt
 sleep 0.5
done

# Generate messages for advanced error handling
for i in {1..5}; do
 echo "VALIDATE - Load test validation message $i" > input/advanced/load-test-validate-$i.txt
 echo "RUNTIME - Load test runtime message $i" > input/advanced/load-test-runtime-$i.txt
 echo "Normal load test message $i - $(date)" > input/advanced/load-test-normal-$i.txt
 sleep 0.5
done

echo "Load test messages generated successfully!"
echo "Monitor the processing with: ./monitor-processing.sh"
