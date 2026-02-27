#!/bin/bash
echo "Starting ActiveMQ Broker..."
java -cp "$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout):target/classes" \
org.apache.activemq.broker.BrokerService &
echo "Broker started in background"
