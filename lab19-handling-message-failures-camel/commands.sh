#!/bin/bash
# Lab 19 - Handling Message Failures with Apache Camel
# Commands Executed During Lab (sequential, no explanations)

mkdir camel-error-handling-lab
cd camel-error-handling-lab

mkdir -p src/main/java/com/example/camel
mkdir -p src/main/resources
mkdir -p src/test/java

nano pom.xml

nano src/main/java/com/example/camel/FailureSimulatorProcessor.java
nano src/main/java/com/example/camel/RetryRouteBuilder.java
nano src/main/java/com/example/camel/DeadLetterQueueRouteBuilder.java

nano src/main/java/com/example/camel/FallbackProcessor.java
nano src/main/java/com/example/camel/FallbackRouteBuilder.java

nano src/main/java/com/example/camel/AdvancedErrorHandlingRouteBuilder.java

nano src/main/java/com/example/camel/ErrorHandlingApplication.java

mkdir -p input/retry input/dlq input/fallback input/fallback-alt input/advanced
mkdir -p output/retry output/dlq/success output/dlq/failed
mkdir -p output/fallback/primary output/fallback/fallback output/fallback/alternative output/fallback/altprimary
mkdir -p output/fallback/alt-primary
mkdir -p output/advanced/success output/advanced/validation-errors output/advanced/runtime-errors
mkdir -p output/advanced/general-errors

mvn clean compile
mvn package -DskipTests

echo "Test message for retry strategy - Message 1" > input/retry/retry-test-1.txt
echo "Test message for retry strategy - Message 2" > input/retry/retry-test-2.txt
echo "Test message for retry strategy - Message 3" > input/retry/retry-test-3.txt

mvn exec:java -Dexec.mainClass="com.example.camel.ErrorHandlingApplication" > nohup.out 2>&1 &

cd ~/camel-error-handling-lab
tail -f nohup.out

ls -la output/retry/

echo "DLQ test message - Message 1" > input/dlq/dlq-test-1.txt
echo "DLQ test message - Message 2" > input/dlq/dlq-test-2.txt
echo "DLQ test message - Message 3" > input/dlq/dlq-test-3.txt

watch -n 2 'ls -la output/dlq/success/ && echo "---" && ls -la output/dlq/failed/'
^C

cat output/dlq/failed/*.txt

echo "Fallback test message - Primary processing" > input/fallback/fallback-test-1.txt
echo "Fallback test message - Should trigger fallback" > input/fallback/fallback-test-2.txt

echo "Alternative fallback test - Message 1" > input/fallback-alt/alt-test-1.txt
echo "Alternative fallback test - Message 2" > input/fallback-alt/alt-test-2.txt

watch -n 2 'echo "Primary:" && ls -la output/fallback/primary/ && echo "Fallback:" && ls -la output/fallback/fallback/'
^C

echo "VALIDATE - This should trigger validation error" > input/advanced/validation-test.txt
echo "RUNTIME - This should trigger runtime error" > input/advanced/runtime-test.txt
echo "GENERAL - This should trigger general error" > input/advanced/general-test.txt
echo "Normal message for processing" > input/advanced/normal-test.txt

watch -n 2 'echo "Success:" && ls -la output/advanced/success/ && echo "Validation Errors:" && ls -la output/advanced/validation-errors/ && echo "Runtime Errors:" && ls -la output/advanced/runtime-errors/ && echo "General Errors:" && ls -la output/advanced/general-errors/'
^C

nano monitor-processing.sh
chmod +x monitor-processing.sh
./monitor-processing.sh

nano analyze-performance.sh
chmod +x analyze-performance.sh

which bc || sudo apt-get update && sudo apt-get install -y bc

./analyze-performance.sh

nano load-test.sh
chmod +x load-test.sh
./load-test.sh

ps aux | grep ErrorHandlingApplication
kill -9 4638

mvn exec:java -Dexec.mainClass="com.example.camel.ErrorHandlingApplication" > application.log 2>&1 &

./load-test.sh
sleep 30
./analyze-performance.sh

grep -i "retry" application.log | head
grep -i "dlq" application.log | head
grep -i "fallback" application.log | head
grep -i "exception" application.log | head

nano generate-test-report.sh
chmod +x generate-test-report.sh
./generate-test-report.sh

cat error-handling-test-report.txt
